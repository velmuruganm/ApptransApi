package me.tomassetti.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.common.base.Strings;

import me.tomassetti.support.DirExplorer;


public class ListMethods {
	static List<String> listOfControllerClasses = new ArrayList<String>();
	static String INIT = "Controller";

	public static Map<String, List<String>> getClassesWithMethodNames(File projectDir) {

		Map<String, List<String>> collector = new HashMap<String, List<String>>();
		collector.put(INIT, new ArrayList<String>()); // equivalent to init

		new DirExplorer((level, path, file) -> path.endsWith("Controller.java"), (level, path, file) ->

		{
			System.out.println(path);
			System.out.println(Strings.repeat("=", path.length()));

			try {

				new VoidVisitorAdapter<Map<String, List<String>>>() {

					@Override
					public void visit(ClassOrInterfaceDeclaration n, Map<String, List<String>> arg) {
						super.visit(n, arg);
						System.out.println(" ClassName   " + n.getName() + "String " + n.getNameAsString()
								+ " Fully Qualified name " + n.getFullyQualifiedName().get());
						collector.get(INIT).add(n.getFullyQualifiedName().get());
						String classNameCollected = n.getFullyQualifiedName().get();
						;

						List<String> methodNameList = new ArrayList<String>();
						collector.put(classNameCollected, methodNameList);// add empty methods if no class in this class
						n.getMethods().forEach(method -> {
							System.out.println("Method name : " + method.getName() + " Declaration "
									+ method.getDeclarationAsString());
							System.out.println("Method type : " + method.getType());
							String methodName = method.getDeclarationAsString().split("\\s")[2]; // method.getNameAsString();
							String methodNameCollected = methodName.split("\\(")[0];
//									.concat("->"+classNameCollected);		// shows only method name
//						String methodNameCollected = method.getDeclarationAsString(); // method.getNameAsString();
																		// shows only method name

							collector.get(classNameCollected).add(methodNameCollected);

						}); // each method for Each ends.

					}

				}.visit(getCompilationUnit(file), collector); // change to collector.
				System.out.println(); // empty line
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		).explore(projectDir);

		return collector;
	}

	@SuppressWarnings("deprecation")
	public static CompilationUnit getCompilationUnit(File file) throws FileNotFoundException {

		CombinedTypeSolver combinedTypeSolver = getCombinedTypeSolver();

		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

		final ParserConfiguration config = new ParserConfiguration().setStoreTokens(true)
				.setSymbolResolver(symbolSolver);

		StaticJavaParser.setConfiguration(config);
		return StaticJavaParser.parse(file);

	}

	public static CombinedTypeSolver getCombinedTypeSolver() {
		TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
		TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(filePath + "/main/java");
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
		return combinedTypeSolver;
	}

	public static void main(String[] args) {
		File projectDir = new File(filePath);
		Map<String, List<String>> map = getClassesWithMethodNames(projectDir);
		analyser(map);
		
	
	}
	
	public Map<String, List<String>> getControllers() {
		File projectDir = new File(filePath);
		Map<String, List<String>> map = getClassesWithMethodNames(projectDir);
		Set<String> keySet = map.keySet();

		for (String key : keySet) {
			System.out.println("Key : " + key + "   Value : " + map.get(key));
		}
		return map;
	}
	
	
	public static ParentItem analyser(Map<String, List<String>> map) {
		
		Set<String> keySet = map.keySet();
		ParentItem root = new ParentItem("root", "class");
		for (String className : keySet)
		{
			ParentItem cls = new ParentItem(className, "class");
				for(String methodName : map.get(className))
				{
					ChildItem chd = new ChildItem(methodName, "method");
					cls.Add(chd);
					System.out.println(chd);
//					System.out.println("name :" + chd.name + 
//							" and type :" + chd.type);
				}
		root.Add(cls);
		}
		System.out.println(root);
//		System.out.println("name :" + root.name + 
//                " and type :" + root.type);
		return root;
	}
	
	public ParentItem sourceanalyser() {
		File projectDir = new File(filePath);
		Map<String, List<String>> map = getClassesWithMethodNames(projectDir);
		ParentItem results =   analyser(map);
		return results;
		
	}

	public static List<String> getJarFolders() {

		String repositoryLocation = "C:\\Users\\44976\\.m2\\repository";
		return Arrays.asList(

				repositoryLocation
						+ "\\org\\springframework\\spring-core\\4.3.18.RELEASE\\spring-core-4.3.18.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\spring-webmvc\\4.3.18.RELEASE\\spring-webmvc-4.3.18.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\spring-aop\\4.3.18.RELEASE\\spring-aop-4.3.18.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\spring-beans\\4.3.18.RELEASE\\spring-beans-4.3.18.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\spring-expression\\4.3.18.RELEASE\\spring-expression-4.3.18.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\spring-web\\4.3.18.RELEASE\\spring-web-4.3.18.RELEASE.jar",

				repositoryLocation + "\\commons-logging\\commons-logging\\1.2\\commons-logging-1.2.jar",
				repositoryLocation
						+ "\\org\\springframework\\spring-orm\\4.3.18.RELEASE\\spring-orm-4.3.18.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\spring-jdbc\\4.3.18.RELEASE\\spring-jdbc-4.3.18.RELEASE.jar",
				repositoryLocation + "\\org\\springframework\\spring-tx\\4.3.18.RELEASE\\spring-tx-4.3.18.RELEASE.jar",
				repositoryLocation + "\\jstl\\jstl\\1.2\\jstl-1.2.jar",
				repositoryLocation + "\\taglibs\\standard\\1.1.2\\standard-1.1.2.jar",
				repositoryLocation + "\\org\\hibernate\\hibernate-core\\4.0.1.Final\\hibernate-core-4.0.1.Final.jar",
				repositoryLocation + "\\commons-collections\\commons-collections\\3.2.1\\commons-collections-3.2.1.jar",
				repositoryLocation + "\\antlr\\antlr\\2.7.7\\antlr-2.7.7.jar",
				repositoryLocation
						+ "\\org\\jboss\\spec\\javax\\transaction\\jboss-transaction-api_1.1_spec\\1.0.0.Final\\jboss-transaction-api_1.1_spec-1.0.0.Final.jar",
				repositoryLocation + "\\dom4j\\dom4j\\1.6.1\\dom4j-1.6.1.jar",
				repositoryLocation + "\\xml-apis\\xml-apis\\1.0.b2\\xml-apis-1.0.b2.jar",
				repositoryLocation + "\\org\\jboss\\logging\\jboss-logging\\3.1.0.CR2\\jboss-logging-3.1.0.CR2.jar",
				repositoryLocation + "\\org\\javassist\\javassist\\3.15.0-GA\\javassist-3.15.0-GA.jar",
				repositoryLocation
						+ "\\org\\hibernate\\javax\\persistence\\hibernate-jpa-2.0-api\\1.0.1.Final\\hibernate-jpa-2.0-api-1.0.1.Final.jar",
				repositoryLocation + "\\com\\h2database\\h2\\1.4.191\\h2-1.4.191.jar",
				repositoryLocation + "\\javax\\servlet\\servlet-api\\2.5\\servlet-api-2.5.jar",
				//repositoryLocation
						//+ "\\org\\hibernate\\hibernate-validator\\5.2.4.Final\\hibernate-validator-5.2.4.Final.jar",
				//repositoryLocation + "\\com\\fasterxml\\classmate\\1.1.0\\classmate-1.1.0.jar",
				repositoryLocation + "\\javax\\validation\\validation-api\\1.1.0.Final\\validation-api-1.1.0.Final.jar",
				repositoryLocation
						+ "\\org\\springframework\\spring-context\\4.3.18.RELEASE\\spring-context-4.3.18.RELEASE.jar",
				repositoryLocation
						+ "\\org\\hibernate\\common\\hibernate-commons-annotations\\4.0.1.Final\\hibernate-commons-annotations-4.0.1.Final.jar",
				repositoryLocation + "\\commons-fileupload\\commons-fileupload\\1.2.2\\commons-fileupload-1.2.2.jar",
				repositoryLocation + "\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar",
				repositoryLocation
						+ "\\org\\springframework\\security\\spring-security-config\\4.1.5.RELEASE\\spring-security-config-4.1.5.RELEASE.jar",
				repositoryLocation + "\\aopalliance\\aopalliance\\1.0\\aopalliance-1.0.jar",
				repositoryLocation
						+ "\\org\\springframework\\security\\spring-security-core\\4.1.5.RELEASE\\spring-security-core-4.1.5.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\security\\spring-security-taglibs\\4.1.5.RELEASE\\spring-security-taglibs-4.1.5.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\security\\spring-security-acl\\4.1.5.RELEASE\\spring-security-acl-4.1.5.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\security\\spring-security-web\\4.1.5.RELEASE\\spring-security-web-4.1.5.RELEASE.jar",
//				repositoryLocation + "\\com\\fasterxml\\jackson\\core\\jackson-core\\2.9.8\\jackson-core-2.9.8.jar",
//				repositoryLocation
//						+ "\\com\\fasterxml\\jackson\\core\\jackson-annotations\\2.9.8\\jackson-annotations-2.9.8.jar",
//				repositoryLocation
//						+ "\\com\\fasterxml\\jackson\\core\\jackson-databind\\2.9.8\\jackson-databind-2.9.8.jar",
				repositoryLocation
						+"\\com\\sun\\xml\\fastinfoset\\FastInfoset\\1.2.12\\FastInfoset-1.2.12.jar",
				repositoryLocation
						+ "\\org\\springframework\\webflow\\spring-webflow\\2.3.3.RELEASE\\spring-webflow-2.3.3.RELEASE.jar",
				repositoryLocation + "\\opensymphony\\ognl\\2.6.11\\ognl-2.6.11.jar",
				repositoryLocation
						+ "\\org\\springframework\\webflow\\spring-binding\\2.3.3.RELEASE\\spring-binding-2.3.3.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\webflow\\spring-js\\2.3.3.RELEASE\\spring-js-2.3.3.RELEASE.jar",
				repositoryLocation
						+ "\\org\\springframework\\webflow\\spring-js-resources\\2.3.3.RELEASE\\spring-js-resources-2.3.3.RELEASE.jar");
	}

	public final static String filePath = "source_to_parse/ShoppingCart-master/src"; // TODO: Change to location of source parse here.

}
