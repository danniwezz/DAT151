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
  LinkedList<Map<String,Integer>> cxt;

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

  // Share type constants
  public final Type BOOL   = new Type_bool  ();
  public final Type INT    = new Type_int   ();
  public final Type DOUBLE = new Type_double();
  public final Type VOID   = new Type_void  ();

  // name should be just the class name without file extension.
  public void compile(String name, CPP.Absyn.Program p) { 

     File file = new File(name);
    
    String dirPath = file.getAbsolutePath();
   
    System.out.println("name: " + name);



    String cname = file.getName().replaceFirst("[.].*", "");
    // Initialize output
    output = new LinkedList();

    // Output boilerplate
    output.add(".class public " + cname + "\n");
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
    output.add("  invokestatic " + cname + "/main()I\n");
    output.add("  pop\n");
    output.add("  return\n");
    output.add("\n");
    output.add(".end method\n");
    output.add("\n");

    // Create signature
    sig = new TreeMap<String, Fun> ();
    // Built-in functions
    ListArg    intArg = 	new ListArg();    intArg.add (new ADecl(INT   , "x"));
    sig.put("printInt",new Fun("Runtime/printInt", new FunType (VOID, intArg)));
    sig.put("readInt", new Fun("Runtime/readInt", new FunType (INT,new  ListArg())));

    // User-defined functions
    for (Def d: ((PDefs)p).listdef_) {
      DFun def = (DFun)d;
      sig.put(def.id_,
        new Fun(cname + "/" + def.id_, new FunType(def.type_, def.listarg_)));
    }

    // Run compiler
    p.accept(new ProgramVisitor(), null);
   

   


    String filePath = file.getAbsolutePath();

    String [] dirs = filePath.split("/");


    String daPath = "";

    for(int i = 0; i< dirs.length - 1; i++){
     if(i == dirs.length - 2){
       daPath += dirs[i];
     }else{
       daPath += dirs[i] + "/";
     }
    }

    System.out.println("daPath" + daPath);
   // String dirPath = filePath.substring(0,filePath.lastIndexOf(File.separator));
    String jasminFile = daPath + File.separator + cname + ".j";
    System.out.println("cname: " + cname);
    System.out.println("filepath: " + filePath);
    
    System.out.println("Jasminfile: " + jasminFile);
    System.out.println("dirpath: " + dirPath);
    try{
    	PrintWriter writer = new PrintWriter(jasminFile);
    	
      for (String s: output) {
      
          writer.write(s);
        }
        
        writer.close();
      Process process = java.lang.Runtime.getRuntime().exec("java -jar jasmin.jar -d " + daPath + " " + jasminFile);
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
      cxt = new LinkedList<Map<String, Integer>>();
      cxt.add(new TreeMap<String,Integer>());
      nextLocal = 0;
      limitLocals = 0;
      limitStack  = 0;
      currentStack = 0;
     

      // save output so far and reset output;
      LinkedList<String> savedOutput = output;
      output = new LinkedList<String>();

      // Compile function

      // Add function parameters to context
      for (Arg x: p.listarg_)
        x.accept (new ArgVisitor(), null);
      for (Stm s: p.liststm_)
        s.accept (new StmVisitor(), null);

      // add new Output to old output
      LinkedList<String> newOutput = output;
      output = savedOutput;
     


      Fun f = new Fun(p.id_, new FunType(p.type_, p.listarg_));

      output.add("\n.method public static " + f.toJVM() + "\n");
      output.add("  .limit locals " + limitLocals + "\n");
      output.add("  .limit stack " + limitStack + "\n\n");
      for (String s: newOutput) {
        output.add("  " + s);
      }

        if(noReturn(p)){
          if(p.id_.equals("main")){
            output.add("  iconst_0"+ "\n");
            output.add("  ireturn");

          }else{
            output.add("  return");
          }
        }

      output.add("\n.end method\n");
      return null;
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
    	emit(new Pop(INT));

      return null;
    }

    // int x,y,z;
    public Void visit(CPP.Absyn.SDecls p, Void arg)
    {
      for (String x: p.listid_){
    	  newVar (x, p.type_);

        if(p.type_.equals(INT)){
          emit(new IConst(0));
          emit(new Store(INT, lookupVar(x)));
        }
      }

      return null;
    }

    // int x = e;
    public Void visit(CPP.Absyn.SInit p, Void arg)
    {
      p.exp_.accept(new ExpVisitor(), null);
      newVar (p.id_, p.type_);

      int adr = lookupVar(p.id_);
      emit (new Store (INT, adr));
      return null;
    }

    // return e;
    public Void visit(CPP.Absyn.SReturn p, Void arg)
    {
      p.exp_.accept (new ExpVisitor(), null);
    	emit (new Return (INT));
    	return null;
    }

    // while (e) s
    public Void visit(CPP.Absyn.SWhile p, Void arg)
    {

      cxt.add(0, new TreeMap<String, Integer>()); // create new block

    	Label startLabel = new Label(createLabel("start"));

    	Label endLabel = new Label(createLabel("end"));
    	emit(startLabel);
    	p.exp_.accept(new ExpVisitor(), null);
    	emit(new IfIcmpEq(endLabel));
    	p.stm_.accept(new StmVisitor(), null);
    	emit(new Goto(startLabel));
    	emit(endLabel);    	
    	
      cxt.remove(0);
      
    	return null;
    }

    // { ss }
    public Void visit(CPP.Absyn.SBlock p, Void arg)
    {
    	cxt.add(0, new TreeMap<String, Integer>()); // create new block

    	for (Stm s: p.liststm_) {
       
    		s.accept(new StmVisitor(), arg);
    	}

    	cxt.remove(0);


    	return null;
    }

    // if (e) s else s'
    public Void visit(CPP.Absyn.SIfElse p, Void arg)
    {
    	Label tLabel = new Label(createLabel("true"));
    	Label fLabel = new Label(createLabel("false"));

    	p.exp_.accept(new ExpVisitor(), null);
    	emit(new IfIcmpEq(fLabel));
	
    	p.stm_1.accept(new StmVisitor(), null);
    	emit(new Goto(tLabel));
    	emit(fLabel);  

	p.stm_2.accept(new StmVisitor(), null);
	emit(tLabel);  	
    	
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
      emit (new Load (INT, lookupVar(p.id_)));
      return null;
    }

    // f (e1, e2)
    public Void visit(CPP.Absyn.EApp p, Void arg)
    {

	Fun f = sig.get(p.id_);
	  if(f==null){
	  throw new RuntimeException(f + " does not exist");
    }
      for (Exp e: p.listexp_){
    	  e.accept(new ExpVisitor(), arg);
      }
      emit (new Call(f));
      return null;
    
    }

/*
    public Void visit(CPP.Absyn.EApp p, Void arg)
    {
      for (Exp e: p.listexp_){
    	  e.accept(new ExpVisitor(), arg);

        	Fun f = sig.get(p.id_);

          emit (new Call(f));

          if(f.funType.returnType.equals(VOID)){
            emit(new IConst(0));
          }
         
      }
       return null;
    }
    */

    // x++
    public Void visit(CPP.Absyn.EPostIncr p, Void arg)
    {    	
      String x = p.id_;
      
      emit (new Load (INT, lookupVar(x)));
      emit (new Dup (INT));
      emit (new IConst (1));
      emit (new Add (INT)); 
      emit (new Store (INT, lookupVar(x)));
      return null;
    }

    // x--
    public Void visit(CPP.Absyn.EPostDecr p, Void arg)
    {
      String x = p.id_;

      emit(new Load(INT, lookupVar(x)));
      emit (new Dup (INT));
      emit (new IConst (1));
      emit (new Sub (INT));
      emit (new Store (INT, lookupVar(x)));
      return null;
    }

    // ++x
    public Void visit(CPP.Absyn.EPreIncr p, Void arg)
    {
      String x = p.id_;

      Integer i = lookupVar(x);
      emit(new Load(INT,i));
      
      emit (new IConst (1));
      emit (new Add (INT));
      emit (new Store (INT, lookupVar(x)));
      emit (new Load(INT, lookupVar(x)));
      return null;
    }

    // --x
    public Void visit(CPP.Absyn.EPreDecr p, Void arg)
    {
      String x = p.id_;

      Integer i = lookupVar(x);
      emit(new Load(INT,i));
      
      emit (new IConst (1));
      emit (new Sub (INT));
      emit (new Store (INT, lookupVar(x)));
      emit (new Load(INT, lookupVar(x)));
      return null;
    }

    // e * e'
    public Void visit(CPP.Absyn.ETimes p, Void arg)
    {
    	
	    p.exp_1.accept(new ExpVisitor(), null);
	    p.exp_2.accept(new ExpVisitor(), null);
	    emit(new Mul(new Type_int()));
	    return null;
    }

    // e / e'
    public Void visit(CPP.Absyn.EDiv p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), null);
	    p.exp_2.accept(new ExpVisitor(), null);
	    emit(new Div(new Type_int()));
	    return null;
    }

    //  e + e'
    public Void visit(CPP.Absyn.EPlus p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), null);
	    p.exp_2.accept(new ExpVisitor(), null);
	    emit(new Add(new Type_int()));
	    return null;
    }

    // e - e'
    public Void visit(CPP.Absyn.EMinus p, Void arg)
    {
    	p.exp_1.accept(new ExpVisitor(), null);
	    p.exp_2.accept(new ExpVisitor(), null);
	    emit(new Sub(new Type_int()));
	    return null;
    }

    // e < e'
    public Void visit(CPP.Absyn.ELt p, Void arg)
    {
	emit(new IConst(1));
    	p.exp_1.accept(new ExpVisitor(), null);
        p.exp_2.accept(new ExpVisitor(), null);

	Label tLabel = new Label(createLabel("True"));
	
        emit(new IfIcmpLt(tLabel));
	emit(new Pop(INT));
	emit(new IConst(0));
	emit(tLabel);
        return null;
    }

    // e > e'
    public Void visit(CPP.Absyn.EGt p, Void arg)
    {
	emit(new IConst(1));
    	p.exp_1.accept(new ExpVisitor(), null);
        p.exp_2.accept(new ExpVisitor(), null);

	Label tLabel = new Label(createLabel("True"));
	
        emit(new IfIcmpGt(tLabel));
	emit(new Pop(INT));
	emit(new IConst(0));
	emit(tLabel);
        return null;
    }

    // e <= e'
    public Void visit(CPP.Absyn.ELtEq p, Void arg)
    {
	emit(new IConst(1));
    	p.exp_1.accept(new ExpVisitor(), null);
        p.exp_2.accept(new ExpVisitor(), null);

	Label tLabel = new Label(createLabel("True"));
	
        emit(new IfIcmpLe(tLabel));
	emit(new Pop(INT));
	emit(new IConst(0));
	emit(tLabel);
        return null;
    }

    // e >= e'
    public Void visit(CPP.Absyn.EGtEq p, Void arg)
    {
	emit(new IConst(1));
    	p.exp_1.accept(new ExpVisitor(), null);
        p.exp_2.accept(new ExpVisitor(), null);

	Label tLabel = new Label(createLabel("True"));
	
        emit(new IfIcmpGe(tLabel));
	emit(new Pop(INT));
	emit(new IConst(0));
	emit(tLabel);
        return null;
    }

    // e == e'
    public Void visit(CPP.Absyn.EEq p, Void arg)
    {
	emit(new IConst(1));
    	p.exp_1.accept(new ExpVisitor(), null);
        p.exp_2.accept(new ExpVisitor(), null);

	Label tLabel = new Label(createLabel("True"));
	
        emit(new IfIcmpEq(tLabel));
	emit(new Pop(INT));
	emit(new IConst(0));
	emit(tLabel);
        return null;
    }

    // e != e'
    public Void visit(CPP.Absyn.ENEq p, Void arg)
    {
	emit(new IConst(1));
    	p.exp_1.accept(new ExpVisitor(), null);
        p.exp_2.accept(new ExpVisitor(), null);

	Label tLabel = new Label(createLabel("True"));
	
        emit(new IfIcmpNe(tLabel));
	emit(new Pop(INT));
	emit(new IConst(0));
	emit(tLabel);
        return null;
    }

    // e && e'
    public Void visit(CPP.Absyn.EAnd p, Void arg)
    {
    	Label fLabel = new Label(createLabel("false"));
    
       Label eLabel = new Label(createLabel("end"));
    
    	p.exp_1.accept(new ExpVisitor(), null);
	    emit(new IfIcmpEq(fLabel));

    	p.exp_2.accept(new ExpVisitor(), null);
    	emit(new IfIcmpEq(fLabel));

      	emit(new IConst(1));

        emit(new Goto(eLabel));

      	emit(fLabel);
        	emit(new IConst(0));
          emit(eLabel);
    	return null;
    }

    // e || e'
    public Void visit(CPP.Absyn.EOr p, Void arg)
    {
       	Label tLabel = new Label(createLabel("true"));
    	emit(new IConst(1));
    	p.exp_1.accept(new ExpVisitor(), null);
    	emit(new IConst(1));
	emit(new IfIcmpEq(tLabel));

    	p.exp_2.accept(new ExpVisitor(), null);
    	emit(new IConst(1));
    	emit(new IfIcmpEq(tLabel));
    	emit(new Pop(INT));
    	emit(new IConst(0));
    	emit(tLabel);     
    	return null;
    }

    // x = e
    public Void visit(CPP.Absyn.EAss p, Void arg)
    {

      p.exp_.accept(new ExpVisitor(), null);
      Integer adr = lookupVar(p.id_);
      emit(new Dup(INT));
      emit(new Store(INT, adr));
      return null;
    }
    }


  void emit (Code c) {
    String line = c.accept(new CodeToJVM());
    if (!line.isEmpty()) output.add(line);
    adjustStack(c);
  }

  void newVar(String x, Type t) {
    cxt.get(0).put(x, nextLocal);

    Integer i = 1;
    nextLocal += i;
    limitLocals += i;
   
  }

  Integer lookupVar (String x) {
    for(Map<String,Integer> m : cxt){

      Integer h = m.get(x); 
 
    	if(m.containsKey(x)){
    		return h;
    	}
    }
    	      throw new RuntimeException ("Impossible: scope is empty");
    
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

  boolean noReturn(DFun fun){
    for(Stm stm : fun.liststm_){
      if(stm instanceof SReturn){
        return false;
      }
    }
    return true;
  }

 String createLabel(String input){

	return input + (nextLabel++);
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
      public Void visit (Store a)  	{ decStack(INT); return null; }
      public Void visit (Load  a)  	{ incStack(INT); return null; }
      public Void visit (IConst c) 	{ incStack(INT);    return null; }
      public Void visit (IfIcmpLt c){ 					return null; }
      public Void visit (IfIcmpLe c){  					return null; }
      public Void visit (IfIcmpEq c){  					return null; }
      public Void visit (IfIcmpGt c){					return null; }
      public Void visit (IfIcmpGe c){ 					return null; }
      public Void visit (IfIcmpNe c){ 					return null; }
      public Void visit (Goto c)	{ 					return null; }
      public Void visit (IfEq c)	{ 					return null; }
      public Void visit (Dup c)     { decStack(INT); return null; }
      public Void visit (Pop c)     { decStack(INT); return null; }
      public Void visit (Return c)  { decStack(INT); return null; }

    public Void visit (Call c)   {
      FunType ft = c.fun.funType;
      for (Arg arg: ft.args) decStack(INT);
      incStack(ft.returnType);
      return null;
    }
    public Void visit (Add c)    { decStack(INT); return null; }
    public Void visit (Mul c)    { decStack(INT); return null; }
    public Void visit (Div c)    { decStack(INT); return null; }
    public Void visit (Sub c)    { decStack(INT); return null; }
  }
}
