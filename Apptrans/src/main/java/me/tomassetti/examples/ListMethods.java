package me.tomassetti.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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

	public static Map<String, List<String>> getClassesWithMethodNames(File projectDir , String filePath) {

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

				}.visit(getCompilationUnit(file, filePath), collector); // change to collector.
				System.out.println(); // empty line
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		).explore(projectDir);

		return collector;
	}

	@SuppressWarnings("deprecation")
	public static CompilationUnit getCompilationUnit( File file, String filePath) throws FileNotFoundException {

		CombinedTypeSolver combinedTypeSolver = getCombinedTypeSolver(filePath);

		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

		final ParserConfiguration config = new ParserConfiguration().setStoreTokens(true)
				.setSymbolResolver(symbolSolver);

		StaticJavaParser.setConfiguration(config);
		return StaticJavaParser.parse(file);

	}

	public static CombinedTypeSolver getCombinedTypeSolver(String filePath) {
		TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
		System.out.println(filePath);
		TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(filePath + "/main/java");
		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
		combinedTypeSolver.add(reflectionTypeSolver);
		combinedTypeSolver.add(javaParserTypeSolver);

		JarFolders.getJarFolders().stream().forEach(jarFile -> {
			try {

				combinedTypeSolver.add(JarTypeSolver.getJarTypeSolver(jarFile));
			} catch (IOException e) {

				e.printStackTrace();
			}
		});
		return combinedTypeSolver;
	}

	public static void main(String[] args) {
		
		final String filePath = args[0];
		File projectDir = new File(args[0]);
		
		Map<String, List<String>> map = getClassesWithMethodNames(projectDir , filePath);
		analyser(map);
		sourceanalyser(args[0]);
		getControllers(args[0]);
		
	
	}
	
	public static Map<String, List<String>> getControllers(String filePath) {
		
		File projectDir = new File(filePath);
		Map<String, List<String>> map = getClassesWithMethodNames(projectDir , filePath);
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
	
	public static ParentItem sourceanalyser(String filePath) {
		File projectDir = new File(filePath);
		Map<String, List<String>> map = getClassesWithMethodNames(projectDir, filePath);
		ParentItem results =   analyser(map);
		return results;
		
	}

	//public final static String filePath = "source_to_parse/ShoppingCart-master/src"; // TODO: Change to location of source parse here.

}
