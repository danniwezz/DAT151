import CPP.Absyn.*;
import java.util.HashMap;
import java.util.LinkedList;

public class TypeChecker {
	public static enum typeCode(){
		Type_int, Type_void, Type_bool, Type_double;
	}

	public void typecheck(Program p) implements Program.Visitor<R,A> {
		PDefs defs = (PDefs) p;
		Env env = new Env();
		
		//Add read/print for ints and doubles
		FunType intRead = new FunType(); 
		intRead.returnType = TypeCode.Type_int ; 
        intRead.args = new LinkedList<TypeCode>() ;
        env.signature.put("intRead", intRead) ;
		
        FunType doubleRead = new FunType(); 
        doubleRead.returnType = TypeCode.Type_double ;
        doubleRead.args = new LinkedList<TypeCode>() ;
        env.signature.put("doubleRead", doubleRead) ;
		
        FunType intPrint = new FunType(); 
		intPrint.returnType = TypeCode.Type_void ;
        intPrint.args = new LinkedList<TypeCode>() ;
        intPrint.args.addLast(TypeCode.Type_int);
        env.signature.put("intPrint", intPrint) ;
		
        FunType doublePrint = new FunType(); 
		doublePrint.returnType = TypeCode.Type_void ;
        doublePrint.args = new LinkedList<TypeCode>() ;
        doublePrint.args.addLast(TypeCode.Type_double);
        env.signature.put("doublePrint", doublePrint) ;

		for(Def def : defs.listdef_){
			//check function decl
			checkFunDef(def, env);
		}
		
		for(Def def : defs.listdef_){
			//check statements in functions
			checkStmDef(def, env);
			if(env.returnFlag == 0){
				throw new TypeException("No return statement");
			}else{
				env.returnFlag = 0;
			}
		}
	}

	private void checkFunDef(Def d, Env env){
		d.accept(new defFunAdd(), env);
	}
	private void checkStmDef(Def d, Env env){
		d.accept(new checkStmDef(), env)
	}
	
	private class defFunAdd implements Def.Visitor<Object , Env> {
		
		public Env visit(DFun p, Env, env){
			if(env.isFunDecl(p.id_)){
				throw new TypeException("Error DFun: The function is already declared");
			}
			
			FunType funType = new FunType();
			funType.args = new LinkedList<TypeCode>();
			funType.returnType = getTypeCode(p.type_);
			
			for( Arg arg : p.listarg_){
				funType.addLast(getTypeCode((ADecl)arg.type_));
			}
			
			env.signature.put(p.id_, funType);
			
			return env;
		}
	}
	
	
	private class checkFunStmDef implements Def.Visitor<Object , Env>{
		
		public Env visit(DFun p, Env, env){
			env.enterScope();
			
			ADecl temp;
			//Beacouse "return" is reserved we add it to the variables, so that it cant be taken
			for(Arg arg : p.listarg_){
				temp = (ADecl)arg;
				env.addVar("return", returnType);
			}
			//Check if the type is void or main, then we should not need a return
				if(returnType == TypeCode.Type_void || p.id_ == "main"){
						env.returnFlag = 1;
				}
			//Add all stm's to the environment
			for(Stm stm : p.liststm_){
				stmCheck(stm, env);
			}
			env.leaveScope();
			
			return env;
		
		}
	}
	
	

	public static class FunType{
		public LinkedList<Type> args;
		public Type val;
	}

	public static class Env{
		public Map<String, FunType> signature;
		public LinkedList<Map<String, Type>> contexts;
		public int returnFlag = 0;

		public Env(){
			contexts = new LinkedList<HashMap<String, TypeCode>();
			signature = new HashMap<String, FunType>();
			returnFlag = 0;
			enterScope();
		};

		public static Type lookVar(String id){
			for (HashMap<String, TypeCode> context : contexts) {
				TypeCode type = context.get(id);
				if (type != null) {
					return type;
				}
			}
			throw new TypeException("There is no [" + id + "] function");
		}

		public boolean isFunDecl(String id){
			return signature.containsKey(id);
		}

		public boolean isVarDecl(String id){
			// just check if the current scope contains the variable
			TypeCode type;
			HashMap<String, TypeCode> context = contexts.getFirst();
			type = context.get(id);
			if (type != null) {
				return true;
			}
			return false;
		}

		public static FunType lookFun(String id){
			FunType funcType = signature.get(id);
			if (funcType == null) {
				throw new TypeException("There is no [" + id + "] function");
			} else {
				return funcType;
			}
		}
//checks if the variable is already declared
		public static void updateVar(String id, Type ty){
			HashMap<String, TypeCode> currentScope = contexts.getFirst();
			if (isVarDecl(id)) {
				throw new TypeException("The variable [ " + id + "] has already been declared");
			} else {
				currentScope.put(id, Ty);
			}
		}

		public void enterScope(){
			contexts.addFirst(new HashMap<String, TypeCode>());
		}

		public void leaveScope(){
			contexts.removeFirst();
		}
	}
	
	private void stmCheck(Stm x, env){
		x.accept(new stmCheck(), env);
	}
	

	// ... checking different statements
	public static class CheckStm implements Stm.Visitor<Env, Env>{
		
		public Env visit(SExp p, Env env) {
			checkExpression(s.exp_, env);
			return env;
		}

		public Env visit(SDecls p, Env env){
			TypeCode type = getTypeCode(p.type_);
			for ( String s : p.listid__){
			env.updateVar(s, type);
			}
			return env;
		}

		public Env visit(SInit p, Env env){
			if(isVarDecl(p.id_)){
				throw new TypeException("SInit error: variable name already exists");
			}
			
			TypeCode eType = checkExpression(p.exp_, env);
			TypeCode tType = getTypeCode(p.type_);
			
			if(eType == tType){
				env.updateVar(p.id_, tType);
			}else{
				throw new TypeException("SInit error: Type does not match");
			}
			return env;
		}

		public Env visit(SReturn p, Env env){
			
			TypeCode eType = checkExpression(p.exp_, env);
			TypeCode rType = getTypeCode(p.type_);
			
			if(eType != tType){
				throw new TypeException("SReturn error: Type does not match");
			}
			
			env.returnFlag = 1;
			
			return env;
		}

		public Env visit(SWhile p, Env env){
			TypeCode eType = checkExpression(p.exp_, env);
			
			if(eType != TypeCode){
				throw new TypeException("SWhile error: Type does not match");
			}
			
			checkExpression(p.stm_, env);
			
			return env;
		}

		public Env visit(SBlock p, Env env){
			
			env.enterScope();
			for(Stm s : p.liststm_){
				stmCheck(s, env);
			}
			
			env.leaveScope();
			
			return env;
		}

		public Env visit(SIfElse p, Env env){
			TypeCode eType = checkExpression(p.exp_, env);
			if(eType != TypeCode.Type_bool){
				throw new TypeException("SIfElse error: Type does not match");
			}
			
			stmCheck(p.stm_1, env);
			stmCheck(p.stm_2, env);
			
			return env;			
		}
	}
	
	public TypeCode checkExpression(Exp exp, Env env){
		return exp.accept(new InferExp(), env);
	}

	public static class InferExp implements Exp.Visitor<Type, Env>{
		public Type visit(ETrue p, Env env) {
			return TypeCode.Type_bool;
		}

		public Type visit(EFalse p, Env env){
			return TypeCode.Type_bool;
		}

		public Type visit(EInt p, Env env){
			return TypeCode.Type_int;
		}

		public Type visit(EDouble p, Env env){
			return TypeCode.Type_double;
		}

		public Type visit(EId p, Env env){
			return env.lookVar(p.id_);
		}

		public Type visit(EApp p, Env env){
			FunType funType = env.lookFun(p.id_);
			int temp = 0, argSize1 = fun.args.size(), argSize2 = p.listexp_.size();
			if(argSize1 != argSize2){
				throw new TypeException("EApp error: Length of args are not the same size");
			}
			for(Exp exp : p.listexp_){
				if(checkExpression(exp, env) != funType.args.get(temp)){
					throw new TypeException("EApp error: "+ tmp + "args doesnt match");
				} else {
					temp++;
				}
				return funType.val;
		}

		public Type visit(EPostIncr p, Env env){
			
			TypeCode type = checkExpression(p.exp_, env);
			if(type == TypeCode.Type_bool){
				throw new TypeException("EPostIncr error : can't increment boolean");
			}
			return type;
		}

		public Type visit(EPostDecr p, Env env){
			TypeCode type = checkExpression(p.exp_, env);
			if(type == TypeCode.Type_bool){
				throw new TypeException("EPostDecr error : can't decrement boolean");
			}
			return type;
		}

		public Type visit(EPreIncr p, Env env){ //ska man inte hantera void också???
			TypeCode type = checkExpression(p.exp_, env);
			if(type == TypeCode.Type_bool){
				throw new TypeException("EPreIncr error : can't increment boolean");
			}
			return type;
			
		}

		public Type visit(EPreDecr p, Env env){
			TypeCode type = checkExpression(p.exp_, env);
			if(type == TypeCode.Type_bool){
				throw new TypeException("EPreDecr error : can't decrement boolean");
			}
			return type;
		}

		public Type visit(ETimes p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("ETimes error : Expressions does not have the same type");
			}else if(type1 == TypeCode.Type_bool || type2 ==  TypeCode.Type_bool){
				throw new TypeException("ETimes error : Expression is boolean");
			}
			return type2;
		}

		public Type visit(EDiv p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("EDiv error : Expressions does not have the same type");
			}else if(type1 == TypeCode.Type_bool || type2 ==  TypeCode.Type_bool){
				throw new TypeException("EDiv error : Expression is boolean");
			}
			return type2;
		}

		public Type visit(EPlus p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("EPlus error : Expressions does not have the same type");
			}else if(type1 == TypeCode.Type_bool || type2 ==  TypeCode.Type_bool){
				throw new TypeException("EPlus error : Expression is boolean");
			}
			return type2;
		}
	

		public Type visit(EMinus p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("EMinus error : Expressions does not have the same type");
			}else if(type1 == TypeCode.Type_bool || type2 ==  TypeCode.Type_bool){
				throw new TypeException("EMinus error : Expression is boolean");
			}
			return type2;
		}


		public Type visit(ELt p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("ELt error : Expressions does not have the same type");
			}
			return TypeCode.Type_bool;
		}

		public Type visit(EGt p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("EGt error : Expressions does not have the same type");
			}
			return TypeCode.Type_bool;
		}

		public Type visit(ELtEg p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("ELtEg error : Expressions does not have the same type");
			}
			return TypeCode.Type_bool;
		}

		public Type visit(EGtEq p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("EGtEq error : Expressions does not have the same type");
			}
			return TypeCode.Type_bool;
		}

		public Type visit(EEq p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("EEq error : Expressions does not have the same type");
			}
			return TypeCode.Type_bool;
		}

		public Type visit(ENEq p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != type2){
				throw new TypeException("ENEq error : Expressions does not have the same type");
			}
			return TypeCode.Type_bool;
		}

		public Type visit(EAnd p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != TypeCode.Type_bool || type2 != TypeCode.Type_bool ){
				throw new TypeException("EAnd error : Expressions must be of type Boolean");
			}
			return TypeCode.Type_bool;
		}

		public Type visit(EOr p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 != TypeCode.Type_bool || type2 != TypeCode.Type_bool ){
				throw new TypeException("EOr error : Expressions must be of type Boolean");
			}
			return TypeCode.Type_bool;
		}

		public Type visit(EAss p, Env env){
			TypeCode type1 = checkExp(p.exp_1, env);
			TypeCode type2 = checkExp(p.exp_2, env);
			
			if(type1 !=  type2){
				throw new TypeException("EAss error : Expressions does not have the same type");
			}
			return type2;
		}

		// ... inferring types of different expressions
	}
    // function for TypeCode
    private TypeCode getTypeCode(Type type)
    {
        return type.accept(new TypeCoder(), null);
    }
    private class TypeCoder implements Type.Visitor<TypeCode , Object>
    {
        public TypeCode visit(Type_bool type, Object arg)
        {
            return TypeCode.Type_bool; 
        }
        public TypeCode visit(Type_int type, Object arg)
        {
            return TypeCode.Type_int;
        }
        public TypeCode visit(Type_double type, Object arg)
        {
            return TypeCode.Type_double;
        }
        public TypeCode visit(Type_void type, Object arg)
        {
            return TypeCode.Type_void;
        }

}

	// ...
}
/* IS DIS HOW IT IS SUPPOSED TO BE DUN?????
public static class InferExpType implements Exp.Visitor<Type, Env>{
	public Type visit(demo.Absyn.EPlus p, Env env){
		Type t1 = p.exp_1.accept(this, env);
		Type t2 = p.exp_2.accept(this, env);
		if (typeCode(t1) == TypeCode.CInt && typeCode(t2) == TypeCode.CInt)
			return TInt;
		else if (typeCode(t1) == TypeCode.CDouble && typeCode(t2) == TypeCode.CDouble)
			return TDouble;
		else
			throw new TypeException("Operands to + must be int or double.");
	}
	// ...
}
*/
	
	
}
