import java.io.*;
import java.util.*;
import CPP.Absyn.*;

public class Compiler
{
  // The output of the compiler is a list of strings.
  LinkedList<String> output;

  // Signature mapping function names to their JVM name and type
  Map<String,Fun> sig;

  // Context mapping variable identifiers to their type.
  LinkedList<Map<String,CxtEntry>> cxt;

  // Next free address for local variable;
  int nextLocal = 0;

  // Number of locals needed for current function
  int limitLocals = 0;

  // Maximum stack size needed for current function
  int limitStack = 0;

  // Current stack size
  int currentStack = 0;

  // Global counter to get next label;
  int nextLabel = 0;
  
  //returnflag
  boolean returnFlag = false;

  // Variable information
  public class CxtEntry {
    final Type    type;
    final Integer addr;
    CxtEntry (Type t, Integer a) { type = t; addr = a; }
  }

  // Share type constants
  public final Type BOOL   = new Type_bool  ();
  public final Type INT    = new Type_int   ();
  public final Type DOUBLE = new Type_double();
  public final Type VOID   = new Type_void  ();

  // name should be just the class name without file extension.
  public void compile(String name, CPP.Absyn.Program p, String filePath) {
    // Initialize output
    output = new LinkedList();

    // Output boilerplate
    output.add(".class public " + name + "\n");
    output.add(".super java/lang/Object\n");
    output.add("\n");
    output.add(".method public <init>()V\n");
    output.add("  .limit locals 1\n");
    output.add("\n");
    output.add("  aload_0\n");
    output.add("  invokespecial java/lang/Object/<init>()V\n");
    output.add("  return\n");
    output.add("\n");
    output.add(".end method\n");
    output.add("\n");
    output.add(".method public static main([Ljava/lang/String;)V\n");
    output.add("  .limit locals 1\n");
    output.add("  .limit stack  1\n");
    output.add("\n");
    output.add("  invokestatic " + name + "/main()I\n");
    output.add("  pop\n");
    output.add("  return\n");
    output.add("\n");
    output.add(".end method\n");
    output.add("\n");

    // Create signature
    sig = new TreeMap();
    // Built-in functions
    ListArg    intArg = 	new ListArg();    intArg.add (new ADecl(INT   , "x"));
    sig.put("printInt",new Fun("Runtime/printInt", new FunType (VOID, intArg)));
    sig.put("readInt", new Fun("Runtime/readInt", new FunType (INT,new  ListArg())));

    // User-defined functions
    for (Def d: ((PDefs)p).listdef_) {
      DFun def = (DFun)d;
      sig.put(def.id_,
        new Fun(name + "/" + def.id_, new FunType(def.type_, def.listarg_)));
    }

    // Run compiler
    p.accept(new ProgramVisitor(), null);
    File file = new File(filePath+".j");
    try{
    	PrintWriter writer = new PrintWriter("kalle" + ".j");
    	
        for (String s: output) {
          //System.out.print(s);
          writer.write(s);
        }
        
        writer.close();
        //Process process = java.lang.Runtime.getRuntime().exec("java -jar jasmin.jar " + filePath + ".j");
    }catch(IOException e){
    	e.printStackTrace();
    }

  }

  public class ProgramVisitor implements Program.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.PDefs p, Void arg)
    {
      for (Def def: p.listdef_)
      {
        def.accept(new DefVisitor(), null);
      }
      return null;
    }
  }
  public class DefVisitor implements Def.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.DFun p, Void arg)
    {
      // reset state for new function
      cxt = new LinkedList();
      cxt.add(new TreeMap());
      nextLocal = 0;
      limitLocals = 0;
      limitStack  = 0;
      currentStack = 0;

      // save output so far and reset output;
      LinkedList<String> savedOutput = output;
      output = new LinkedList();

      // Compile function

      // Add function parameters to context
      for (Arg x: p.listarg_)
        x.accept (new ArgVisitor(), null);
      for (Stm s: p.liststm_)
        s.accept (new StmVisitor(), null);

      // add new Output to old output
      LinkedList<String> newOutput = output;
      output = savedOutput;
      returnFlag = true;
      
      if(p.type_ instanceof Type_void || p.id_.equals("main")){
    	  returnFlag = false;
      }

      Fun f = new Fun(p.id_, new FunType(p.type_, p.listarg_));
      output.add("\n.method public static " + f.toJVM() + "\n");
      output.add("  .limit locals " + limitLocals + "\n");
      output.add("  .limit stack " + limitStack + "\n\n");
      for (String s: newOutput) {
        output.add("  " + s);
      }
      if(p.type_ instanceof Type_void){
    	  output.add("return");
      }
      if(!returnFlag){
    	  finalReturn(new Type_int());
      }
      output.add("\n.end method\n");
      return null;
    }
  }

  public void finalReturn (Type t) {
    emit (new Comment("default return\n"));
    if (!(t instanceof Type_void)){
    	emit(new IConst(0));
    	emit(new Return(t));
    }
  }

  public class ArgVisitor implements Arg.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.ADecl p, Void arg)
    {
      newVar (p.id_, p.type_);
      return null;
    }
  }


  public class StmVisitor implements Stm.Visitor<Void,Void>
  {
    // e;
    public Void visit(CPP.Absyn.SExp p, Void arg)
    {
      p.exp_.accept (new ExpVisitor(), null);
      return null;
    }

    // int x,y,z;
    public Void visit(CPP.Absyn.SDecls p, Void arg)
    {
      for (String x: p.listid_){
    	  emit(new IConst(0));
    	  newVar (x, p.type_);
    	  CxtEntry ce = lookupVar(x);
    	  emit(new Store(p.type_, ce.addr));
      }
      return null;
    }

    // int x = e;
    public Void visit(CPP.Absyn.SInit p, Void arg)
    {
      newVar (p.id_, p.type_);
      p.exp_.accept(new ExpVisitor(), null);
      CxtEntry ce = lookupVar(p.id_);
      emit (new Store (ce.type, ce.addr));
      return null;
    }

    // return e;
    public Void visit(CPP.Absyn.SReturn p, Void arg)
    {
    	p.exp_.accept (new ExpVisitor(), null);
    	emit (new Return (INT));
    	returnFlag = true;
    	return null;
    }

    // while (e) s
    public Void visit(CPP.Absyn.SWhile p, Void arg)
    {
    	int startLabel = nextLabel;
    	nextLabel++;
    	int endLabel = nextLabel;
    	nextLabel++;
    	emit(new Label(startLabel));
    	p.exp_.accept(new ExpVisitor(), null);
    	emit(new IConst(0));
    	emit(new IfIcmpEq(startLabel));
    	p.stm_.accept(new StmVisitor(), null);
    	emit(new Goto(startLabel));
    	emit(new Label(endLabel));    	
    	
    	
    	return null;
    }

    // { ss }
    public Void visit(CPP.Absyn.SBlock p, Void arg)
    {
    	cxt.add(0, new TreeMap());
    	for (Stm s: p.liststm_) {
    		s.accept(new StmVisitor(), arg);
    	}
    	cxt.remove(0);
    	return null;
    }

    // if (e) s else s'
    public Void visit(CPP.Absyn.SIfElse p, Void arg)
    {
    	Integer elseLabel = nextLabel;
    	nextLabel++;
    	Integer endLabel = nextLabel;
    	nextLabel++;
    	
	    p.exp_.accept(new ExpVisitor(), arg);
	    emit(new IConst(0));
	    p.stm_1.accept(new StmVisitor(), arg);
	    emit(new Goto(endLabel));
	    p.stm_2.accept(new StmVisitor(), arg);
	    emit(new Label(endLabel));
	    return null;
    }
  }


  public class ExpVisitor implements Exp.Visitor<Void,Void>
  {
    // true
    public Void visit(CPP.Absyn.ETrue p, Void arg)
    {
    	emit(new IConst(1));
    	return null;
    }

    // false
    public Void visit(CPP.Absyn.EFalse p, Void arg)
    {
    	emit(new IConst(0));
    	return null;
    }

    // 5
    public Void visit(CPP.Absyn.EInt p, Void arg)
    {
      emit (new IConst (p.integer_));
      return null;
    }

    // 3.14
    public Void visit(CPP.Absyn.EDouble p, Void arg)
    {
      // p.double_
      return null;
    }

    // x
    public Void visit(CPP.Absyn.EId p, Void arg)
    {
      CxtEntry ce = lookupVar (p.id_);
      emit (new Load (ce.type, ce.addr));
      return null;
    }

    // f (e1, e2)
    public Void visit(CPP.Absyn.EApp p, Void arg)
    {
      for (Exp e: p.listexp_){
    	  e.accept(new ExpVisitor(), arg);
      }
      Fun f = sig.get(p.id_);
      emit (new Call(f));
      return null;
    }

    // x++
    public Void visit(CPP.Absyn.EPostIncr p, Void arg)
    {    	
      String x = p.id_;
      CxtEntry ce = lookupVar (x);
      emit (new Load (ce.type, ce.addr));
      emit (new Dup (ce.type));
      emit (new IConst (1));
      emit (new Add (ce.type));
      emit (new Store (ce.type, ce.addr));
      return null;
    }

    // x--
    public Void visit(CPP.Absyn.EPostDecr p, Void arg)
    {
    	String x = p.id_;
        CxtEntry ce = lookupVar (x);
        emit (new Load (ce.type, ce.addr));
        emit (new Dup (ce.type));
        emit (new IConst (1));
        emit (new Sub (ce.type));
        emit (new Store (ce.type, ce.addr));
        return null;
    }

    // ++x
    public Void visit(CPP.Absyn.EPreIncr p, Void arg)
    {
    	String x = p.id_;
        CxtEntry ce = lookupVar (x);
        emit (new Load (ce.type, ce.addr));
        emit (new IConst (1));
        emit (new Add (ce.type));
        emit (new Store (ce.type, ce.addr));
        emit (new Load  (ce.type, ce.addr));
        return null;
    }

    // --x
    public Void visit(CPP.Absyn.EPreDecr p, Void arg)
    {
    	String x = p.id_;
        CxtEntry ce = lookupVar (x);
        emit (new Load (ce.type, ce.addr));
        emit (new IConst (1));
        emit (new Sub (ce.type));
        emit (new Store (ce.type, ce.addr));
        emit (new Load  (ce.type, ce.addr));
        return null;
    }

    // e * e'
    public Void visit(CPP.Absyn.ETimes p, Void arg)
    {
    	
	    p.exp_1.accept(new ExpVisitor(), arg);
	    p.exp_2.accept(new ExpVisitor(), arg);
	    emit(new Mul(new Type_int()));
	    return null;
    }

    // e / e'
    public Void visit(CPP.Absyn.EDiv p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
	    p.exp_2.accept(new ExpVisitor(), arg);
	    emit(new Div(new Type_int()));
	    return null;
    }

    //  e + e'
    public Void visit(CPP.Absyn.EPlus p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
	    p.exp_2.accept(new ExpVisitor(), arg);
	    emit(new Add(new Type_int()));
	    return null;
    }

    // e - e'
    public Void visit(CPP.Absyn.EMinus p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
	    p.exp_2.accept(new ExpVisitor(), arg);
	    emit(new Sub(new Type_int()));
	    return null;
    }

    // e < e'
    public Void visit(CPP.Absyn.ELt p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
        p.exp_2.accept(new ExpVisitor(), arg);
        Integer trueLabel = nextLabel;
        nextLabel++;
        Integer falseLabel = nextLabel;
        nextLabel++;
        emit(new IfIcmpLt(nextLabel));
        nextLabel++;
        return null;
    }

    // e > e'
    public Void visit(CPP.Absyn.EGt p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
        p.exp_2.accept(new ExpVisitor(), arg);
        Integer trueLabel = nextLabel;
        nextLabel++;
        Integer falseLabel = nextLabel;
        nextLabel++;
        emit(new IfIcmpGt(nextLabel));
        nextLabel++;
        return null;
    }

    // e <= e'
    public Void visit(CPP.Absyn.ELtEq p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
        p.exp_2.accept(new ExpVisitor(), arg);
        Integer trueLabel = nextLabel;
        nextLabel++;
        Integer falseLabel = nextLabel;
        nextLabel++;
        emit(new IfIcmpLe(nextLabel));
        nextLabel++;
        return null;
    }

    // e >= e'
    public Void visit(CPP.Absyn.EGtEq p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
        p.exp_2.accept(new ExpVisitor(), arg);
        Integer trueLabel = nextLabel;
        nextLabel++;
        Integer falseLabel = nextLabel;
        nextLabel++;
        emit(new IfIcmpGe(nextLabel));
        nextLabel++;
        return null;
    }

    // e == e'
    public Void visit(CPP.Absyn.EEq p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
        p.exp_2.accept(new ExpVisitor(), arg);
        Integer trueLabel = nextLabel;
        nextLabel++;
        Integer falseLabel = nextLabel;
        nextLabel++;
        emit(new IfIcmpEq(nextLabel));
        nextLabel++;
        return null;
    }

    // e != e'
    public Void visit(CPP.Absyn.ENEq p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), arg);
        p.exp_2.accept(new ExpVisitor(), arg);
        Integer trueLabel = nextLabel;
        nextLabel++;
        Integer falseLabel = nextLabel;
        nextLabel++;
        emit(new IfIcmpNe(nextLabel));
        nextLabel++;
        return null;
    }

    // e && e'
    public Void visit(CPP.Absyn.EAnd p, Void arg)
    {
    	Integer falseLabel = nextLabel;
    	nextLabel++;
    	Integer endLabel = nextLabel;
    	nextLabel++;
    	
    	p.exp_1.accept(new ExpVisitor(), arg);
    	emit(new IfEq(falseLabel));
    	p.exp_2.accept(new ExpVisitor(), arg);
    	emit(new IConst(1));
    	emit(new Goto(endLabel));
    	emit(new Label(falseLabel));
    	emit(new IConst(0));
    	emit(new Label(endLabel));     
    	return null;
    }

    // e || e'
    public Void visit(CPP.Absyn.EOr p, Void arg)
    {
    	Integer trueLabel = nextLabel;
    	nextLabel++;
    	Integer endLabel = nextLabel;
    	nextLabel++;
    	
    	p.exp_1.accept(new ExpVisitor(), arg);
    	emit(new IfEq(trueLabel));
    	p.exp_2.accept(new ExpVisitor(), arg);
    	emit(new IConst(0));
    	emit(new Goto(endLabel));
    	emit(new Label(trueLabel));
    	emit(new IConst(1));
    	emit(new Label(endLabel));     
    	return null;
    }

    // x = e
    public Void visit(CPP.Absyn.EAss p, Void arg)
    {
      String x = p.id_;
      CxtEntry ce = lookupVar(p.id_);
      p.exp_.accept(new ExpVisitor(), arg);
      emit(new Store(ce.type, ce.addr));
      emit(new Load(ce.type, ce.addr));
      return null;
    }
  }

  void emit (Code c) {
    String line = c.accept(new CodeToJVM());
    if (!line.isEmpty()) output.add(line);
    adjustStack(c);
  }

  void newVar(String x, Type t) {
    cxt.get(0).put(x, new CxtEntry(t, nextLocal));
    limitLocals++;
    limitStack++;
    nextLocal++;
  }

  CxtEntry lookupVar (String x) {
    for(Map context : cxt){
    	CxtEntry ent = (CxtEntry)context.get(x);
    	if(ent != null){
    		return ent;
    	}else{
    		throw new RuntimeException ("Impossible: scope is empty");
    	}
 
    }
	
    throw new RuntimeException ("Impossible: unbound variable " + x);
  }

  // update limitStack, currentStack according to instruction
  void adjustStack(Code c) {
    c.accept(new AdjustStack());
  }

  void incStack(Type t) {
    currentStack = currentStack + t.accept(new Size(), null);
    if (currentStack > limitStack) limitStack = currentStack;
  }

  void decStack(Type t) {
    currentStack = currentStack - t.accept(new Size(), null);
  }

  // Calculate the size in words for an element of the give type

  class Size implements Type.Visitor<Integer,Void> {
    public Integer visit (Type_double t, Void arg) { return 2; }
    public Integer visit (Type_int    t, Void arg) { return 1; }
    public Integer visit (Type_bool   t, Void arg) { return 1; }
    public Integer visit (Type_void   t, Void arg) { return 0; }
  }

  class AdjustStack implements CodeVisitor<Void> {
	  public Void visit (Label c)  	{ 					return null; }
      public Void visit (Comment a)	{                   return null; }
      public Void visit (Store a)  	{ decStack(a.type); return null; }
      public Void visit (Load  a)  	{ incStack(a.type); return null; }
      public Void visit (IConst c) 	{ incStack(INT);    return null; }
      public Void visit (IfIcmpLt c){ 					return null; }
      public Void visit (IfIcmpLe c){  					return null; }
      public Void visit (IfIcmpEq c){  					return null; }
      public Void visit (IfIcmpGt c){					return null; }
      public Void visit (IfIcmpGe c){ 					return null; }
      public Void visit (IfIcmpNe c){ 					return null; }
      public Void visit (Goto c)	{ 					return null; }
      public Void visit (IfEq c)	{ 					return null; }
      public Void visit (Dup c)     { decStack(c.type); return null; }
      public Void visit (Pop c)     { decStack(c.type); return null; }
      public Void visit (Return c)  { decStack(c.type); return null; }

    public Void visit (Call c)   {
      FunType ft = c.fun.funType;
      for (Arg arg: ft.args) decStack(((ADecl)arg).type_);
      incStack(ft.returnType);
      return null;
    }
    public Void visit (Add c)    { decStack(c.type); return null; }
    public Void visit (Mul c)    { decStack(c.type); return null; }
    public Void visit (Div c)    { decStack(c.type); return null; }
    public Void visit (Sub c)    { decStack(c.type); return null; }
  }
}
