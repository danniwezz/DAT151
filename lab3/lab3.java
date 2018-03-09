import java.io.*;
import java_cup.runtime.*;
import java.lang.*;
import CPP.*;
import CPP.Absyn.*;
import java.util.*;

public class lab3 {
  public static void main(String args[]) {

    if (args.length !=  2) {
      System.err.println("Usage: lab3 <SourceFile>");
      System.exit(1);
    }

    Yylex l = null;
    String targetPath = args[0];
    String filePath = args[0];
    System.out.println("targetPath: " +targetPath);
    System.out.println("filename: " + filePath);
    try {
      l = new Yylex(new FileReader(filePath));
      parser p = new parser(l);
      CPP.Absyn.Program parseTree = p.pProgram();
      new TypeChecker().typecheck(parseTree);
      //String path = new File(filePath).getName();

      new Compiler().compile(stripSuffix(stripPath(filePath)), parseTree, targetPath + "/" + stripSuffix(filePath));
      /*int a = fileName.lastIndexOf("/");
      
      String segments[] = filePath.split("/");


      fileName = fileName.substring(a+1, fileName.length() - 3);
      filePath = segments[0];
      //System.out.println(fileName);
      //System.out.println(filePath);
      new Compiler().compile(fileName, filePath, parseTree);*/
    }
    catch (TypeException e) {
      System.out.println("TYPE ERROR");
      System.err.println(e.toString());
      System.exit(1);
    }
    catch (RuntimeException e) {
      //            System.out.println("RUNTIME ERROR");
      System.err.println(e.toString());
      System.exit(-1);
    }
    catch (IOException e) {
      System.err.println(e.toString());
      System.exit(1);
    }
    catch (Throwable e) {
      System.out.println("SYNTAX ERROR");
      System.out.println("At line " + String.valueOf(l.line_num())
                 + ", near \"" + l.buff() + "\" :");
      System.out.println("     " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  // Remove path from file.
  private static String stripPath(String name) {
    return new File(name).getName();
  }

  // Remove extension from file
  private static String stripSuffix(String filename) {
    int divider = filename.lastIndexOf('.');
    if (divider <= 0) return filename;
    else return filename.substring(0, divider);
  }
}
