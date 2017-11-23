/*import CPP.Absyn.*;
import java.util.HashMap;
import java.util.LinkedList;

public class Interpreter {

    public void interpret(Program p) {
		
		PDefs defs = (PDefs) p ; 
        Env env = new Env() ; 
        for(Def d : defs.listdef_ )
        
    	for(Def def : defs.listdef_){
			//add function exec body, but not the main function
			checkFunDef(def, env);
		}
		
		for(Def def : defs.listdef_){
			//exec main function
			checkMainDef(def, env);
			if(env.returnFlag == 0){
				throw new TypeException("No return statement");
			}else{
				env.returnFlag = 0;
			}
		}
	}

	private void checkFunDef(Def d, Env env){
		d.accept(new defExeAdd(), env);
	}
	private void checkMainDef(Def d, Env env){
		d.accept(new checkMainExec(), env)
	}
	
	private class defExeAdd implements Def.Visitor<Object , Env> {
		
		public Env visit(DFun p, Env, env){
			
	
		}
	}
	
	
	private class checkMainExec implements Def.Visitor<Object , Env>{
		
		public Env visit(DFun p, Env, env){
		
		}
	}
	
	public static class FunExec {
        public LinkedList<String> args ;
        public LinkedList<Stm> liststm_; 
        public Value returnValue; 
    }
	
	
	
	
	
	public static class Env {
        public HashMap<String , FunExe> signature ; 
        public LinkedList<HashMap<String , Value>> contexts ; 
        public Env() 
        {
            contexts = new LinkedList<HashMap<String , Value>>();
            signature = new HashMap<String , FunExe> () ; 
            enterScope(); 
        }
	}
	
	

}
*/