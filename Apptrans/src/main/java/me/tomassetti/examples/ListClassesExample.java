package me.tomassetti.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.base.Strings;
import me.tomassetti.support.DirExplorer;



public class ListClassesExample {
	static List<String> listOfControllerClasses = new ArrayList <String> (); 
	
    public static void listClasses(File projectDir) {
        new DirExplorer((level, path, file) -> path.endsWith("Controller.java"), (level, path, file) -> 
        
        
        {
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));
            try {
                new VoidVisitorAdapter<Object>() {
                	
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        System.out.println(" ClassName " + n.getName());
                        
                       
                        n.findAll(FieldDeclaration.class).forEach(field -> {
                            field.getVariables().forEach(variable -> {
                                //Print the field's class typr
                                System.out.println("Variable Type :=>" + variable.getType());
                               // System.out.println("Full Type  Useful for us :=>" + javaParserFacade.solve (variable.getType().asString()));
                                //Print the field's name
                                System.out.println("Variable Name :=>" + variable.getName());
                                //Print the field's init value, if not null
                                variable.getInitializer().ifPresent(initValue -> System.out.println(initValue.toString()));
                            });
                        });
                        

                      
                                    
                     n.getMethods().forEach (method ->  
                     {
                    	 	System.out.println ("Method name : " + method.getName());
                    	 	 System.out.println("Method type : " + method.getType());
                             
                    	 	
	                    	 	new VoidVisitorAdapter<Object>() { 
	                    	 		 public void visit(MethodCallExpr mce, Object arg) {
	                                     super.visit(mce, arg);
	                                     System.out.println(" [L " + mce.getBegin().get().line + "] " + mce);
	                                     try {
	                                     JavaParserFacade javaParserFacade = JavaParserFacade.get(combinedTypeSolver1);
	                                     SymbolReference<ResolvedMethodDeclaration>  methodRef = javaParserFacade.solve(mce);
	                                     if (methodRef.isSolved()) {
	                                    	 
	                                    	 ResolvedMethodDeclaration  methodDecl = methodRef.getCorrespondingDeclaration();            
	                                    	  System.out.println ("Resolved Method Declaration: " + methodDecl.getQualifiedSignature());
	                                    
	                                     
	                                     }
	                                     
	                                     
	                                     /*
	                                     
	                                     System.out.println ("Called Function: " + mce.getName());
	                                     System.out.println (" Method Call Expression:" + mce);
	                                     System.out.println (" Simple Name of the called function :" + mce.getNameAsString());
	                                     System.out.println (" Parent Node:" +  mce.getParentNode());
	                                     System.out.println (" Child Node:" +  mce.getChildNodes());  
	                                     System.out.println("Type arguments" + mce.getTypeArguments());
	                                     ResolvedType resolvedType = mce.calculateResolvedType();
	                                     System.out.println("Qualified class name " + mce.getMetaModel());
	                                     System.out.println("Signature " + mce.resolve().getSignature());
	                                     System.out.println("Qualified Signature:" + mce.resolve().getQualifiedSignature());
	                                     System.out.println("Qualified Name:" + mce.resolve().getQualifiedName());
	                                    
	                                    */
	                                     
	                                    }
	                                     catch (UnsolvedSymbolException ex) {
	                                    	ex.printStackTrace();
	                                    	//throw ex;
	                                     }
	                                     catch (Exception ex) {
	                                    	 System.out.println ("Generic Exception");
	                                    	 System.out.println (ex.getLocalizedMessage());
	                                    	 //ex.printStackTrace();
	                                     }
	                                     
	                                     Node node = mce.getParentNodeForChildren();
	                                     
	                                                                    }
	                    	 	}
	                    	 	.visit(method, arg);
                     });   
                      
                    }
                    
                    
                   
                   
                }.visit(getCompilationUnit(file), null);
                System.out.println(); // empty line
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        ).explore(projectDir);
        
       
    }



	@SuppressWarnings("deprecation")
	private static CompilationUnit getCompilationUnit(File file) throws FileNotFoundException {
		
		 TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
	        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(filePath + "/main/java");
	        TypeSolver jarTypeSolver = null;
	        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
	        combinedTypeSolver.add(reflectionTypeSolver);
	        combinedTypeSolver.add(javaParserTypeSolver);
	        
		
	        getJarFolders().stream().forEach(jarFile -> {
					try {
						
						combinedTypeSolver.add(JarTypeSolver.getJarTypeSolver(jarFile));
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				});
				
			

	      
	        
	       
	        
	       // combinedTypeSolver.add(JreTypeSolver())
	        //combinedSolver.setParent(reflectionTypeSolver);
	        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
		        
	        combinedTypeSolver1 =combinedTypeSolver;
	        final ParserConfiguration config = new ParserConfiguration()
	                .setStoreTokens(true)
	                .setSymbolResolver(symbolSolver);
	        
	        StaticJavaParser
	               .setConfiguration(config);
		return StaticJavaParser.parse(file);
		
	}
   
    
	 private static void showReferenceTypeDeclaration(ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration){

	        System.out.println(String.format("== %s ==",
	                resolvedReferenceTypeDeclaration.getQualifiedName()));
	        System.out.println(" fields:");
	        resolvedReferenceTypeDeclaration.getAllFields().forEach(f ->
	                System.out.println(String.format("    %s %s", f.getType(), f.getName())));
	        System.out.println(" methods:");
	        resolvedReferenceTypeDeclaration.getAllMethods().forEach(m ->
	                System.out.println(String.format("    %s", m.getQualifiedSignature())));
	        System.out.println();
	    }
    
    public static void main(String[] args) {
        File projectDir =   new File(filePath);
        file = projectDir;
        		//new File("D:/github/analyze-java-code-examples/src/main/java/me/tomassetti/examples");
      
        listClasses(projectDir);
        //listMethodCalls(projectDir);
    }
    
    
    static List<String> getJarFolders () {
    	
    
    	 return Arrays.asList(
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-core\\4.3.18.RELEASE\\spring-core-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-webmvc\\4.3.18.RELEASE\\spring-webmvc-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-aop\\4.3.18.RELEASE\\spring-aop-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-beans\\4.3.18.RELEASE\\spring-beans-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-expression\\4.3.18.RELEASE\\spring-expression-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-web\\4.3.18.RELEASE\\spring-web-4.3.18.RELEASE.jar",
    			 
    			 "C:\\Users\\44976\\.m2\\repository\\commons-logging\\commons-logging\\1.2\\commons-logging-1.2.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-orm\\4.3.18.RELEASE\\spring-orm-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-jdbc\\4.3.18.RELEASE\\spring-jdbc-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-tx\\4.3.18.RELEASE\\spring-tx-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\jstl\\jstl\\1.2\\jstl-1.2.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\taglibs\\standard\\1.1.2\\standard-1.1.2.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\hibernate\\hibernate-core\\4.0.1.Final\\hibernate-core-4.0.1.Final.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\commons-collections\\commons-collections\\3.2.1\\commons-collections-3.2.1.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\antlr\\antlr\\2.7.7\\antlr-2.7.7.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\jboss\\spec\\javax\\transaction\\jboss-transaction-api_1.1_spec\\1.0.0.Final\\jboss-transaction-api_1.1_spec-1.0.0.Final.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\dom4j\\dom4j\\1.6.1\\dom4j-1.6.1.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\xml-apis\\xml-apis\\1.0.b2\\xml-apis-1.0.b2.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\jboss\\logging\\jboss-logging\\3.1.0.CR2\\jboss-logging-3.1.0.CR2.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\javassist\\javassist\\3.15.0-GA\\javassist-3.15.0-GA.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\hibernate\\javax\\persistence\\hibernate-jpa-2.0-api\\1.0.1.Final\\hibernate-jpa-2.0-api-1.0.1.Final.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\com\\h2database\\h2\\1.4.191\\h2-1.4.191.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\javax\\servlet\\servlet-api\\2.5\\servlet-api-2.5.jar",
    			// "C:\\Users\\44976\\.m2\\repository\\org\\hibernate\\hibernate-validator\\5.2.4.Final\\hibernate-validator-5.2.4.Final.jar",
    			// "C:\\Users\\44976\\.m2\\repository\\com\\fasterxml\\classmate\\1.1.0\\classmate-1.1.0.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\javax\\validation\\validation-api\\1.1.0.Final\\validation-api-1.1.0.Final.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\spring-context\\4.3.18.RELEASE\\spring-context-4.3.18.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\hibernate\\common\\hibernate-commons-annotations\\4.0.1.Final\\hibernate-commons-annotations-4.0.1.Final.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\commons-fileupload\\commons-fileupload\\1.2.2\\commons-fileupload-1.2.2.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\security\\spring-security-config\\4.1.5.RELEASE\\spring-security-config-4.1.5.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\aopalliance\\aopalliance\\1.0\\aopalliance-1.0.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\security\\spring-security-core\\4.1.5.RELEASE\\spring-security-core-4.1.5.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\security\\spring-security-taglibs\\4.1.5.RELEASE\\spring-security-taglibs-4.1.5.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\security\\spring-security-acl\\4.1.5.RELEASE\\spring-security-acl-4.1.5.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\security\\spring-security-web\\4.1.5.RELEASE\\spring-security-web-4.1.5.RELEASE.jar",
    			// "C:\\Users\\44976\\.m2\\repository\\com\\fasterxml\\jackson\\core\\jackson-core\\2.9.8\\jackson-core-2.9.8.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\com\\sun\\xml\\fastinfoset\\FastInfoset\\1.2.12\\FastInfoset-1.2.12.jar",
    			// "C:\\Users\\44976\\.m2\\repository\\com\\fasterxml\\jackson\\core\\jackson-annotations\\2.9.8\\jackson-annotations-2.9.8.jar",
    			// "C:\\Users\\44976\\.m2\\repository\\com\\fasterxml\\jackson\\core\\jackson-databind\\2.9.8\\jackson-databind-2.9.8.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\webflow\\spring-webflow\\2.3.3.RELEASE\\spring-webflow-2.3.3.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\opensymphony\\ognl\\2.6.11\\ognl-2.6.11.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\webflow\\spring-binding\\2.3.3.RELEASE\\spring-binding-2.3.3.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\webflow\\spring-js\\2.3.3.RELEASE\\spring-js-2.3.3.RELEASE.jar",
    			 "C:\\Users\\44976\\.m2\\repository\\org\\springframework\\webflow\\spring-js-resources\\2.3.3.RELEASE\\spring-js-resources-2.3.3.RELEASE.jar"
    		);
    	 }
    		
    		
    		
    		
    		
    		
    		
    		
    		
//    		
//    		
//    		
//    		
//    		
    
    public final static String filePath = "source_to_parse/ShoppingCart-master/src";
    public  static File file = null;
    public static TypeSolver combinedTypeSolver1 = null;
}

