package me.tomassetti.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.Git;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
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

public class ClassDependencies {

	public Map<String, Set<String>> listDependantClassesInaFolder( String filePath) {
		Map<String, Set<String>> returnValue = new HashMap<String, Set<String>>();

		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) ->

		{

			System.out.println(path);
			System.out.println(Strings.repeat("=", path.length()));
			try {

				Map<String, Set<String>> collectorPerClass = new HashMap<String, Set<String>>();

				new VoidVisitorAdapter<Map<String, Set<String>>>() {

					@Override
					public void visit(ClassOrInterfaceDeclaration n, Map<String, Set<String>> collector) {
						super.visit(n, collector);

						final String fullyQualifiedClassName = n.getFullyQualifiedName().get();
						Set<String> dependencies = collector.get(fullyQualifiedClassName);
						if (dependencies == null)
							dependencies = new HashSet<String>();
						collector.put(fullyQualifiedClassName, dependencies);

						System.out.println(" ClassName   " + n.getName() + "String " + n.getNameAsString()
								+ " Fully Qualified name " + n.getFullyQualifiedName().get());

						Set<String> classNames =  classNameAndLocation.keySet();
						n.findAll(FieldDeclaration.class).forEach(field -> {
							 field.getVariables().forEach(variable -> {
	                                // Print the field's class type
								 	ResolvedType  resolvedType = variable.getType().resolve();
								 	//resolvedType.isWildcard();
								 	resolvedType.isReference();
	                                String variable_Type = resolvedType.describe();
	                                System.out.println("Variable Type :=>" +  variable_Type);
	                                
	                                Set<String> dep = collector.get(n.getFullyQualifiedName().get());
	                                if (classNames .contains(variable_Type)) {
	                                    dep.add(variable_Type);
	                                    System.out.println("Variable Type added :=>" + variable.getType());
	                                }
	                                // Print the field's name
	                              
	                              
	                                System.out.println("Variable Name :=>" + variable.getName());

	 

	                                variable.getInitializer()
	                                        .ifPresent(initValue -> System.out.println(initValue.toString()));
	                            }); // field.forEach
						}); // n.findAll.forEach

						n.getMethods().forEach(method -> {
							System.out.println("Method name : " + method.getName());
//							System.out.println("Method type : " + method.getType());

							new VoidVisitorAdapter<Map<String, Set<String>>>() {
								public void visit(MethodCallExpr mce, Map<String, Set<String>> arg) {
									super.visit(mce, arg);
									System.out.println(" [L " + mce.getBegin().get().line + "] " + mce);
									try {

										CombinedTypeSolver combinedTypeSolver = getCombinedTypeSolver(new File (filePath), filePath);
										JavaParserFacade javaParserFacade = JavaParserFacade.get(combinedTypeSolver);
										SymbolReference<ResolvedMethodDeclaration> methodRef = javaParserFacade
												.solve(mce);
										if (methodRef.isSolved()) {

											ResolvedMethodDeclaration methodDecl = methodRef
													.getCorrespondingDeclaration();
											System.out.println("Resolved Method Declaration: "
													+ methodDecl.getQualifiedSignature());
											String className = methodDecl.getPackageName() + "."
													+ methodDecl.getClassName();
											System.out
													.println("Resolved Method Declaration Invoking class " + className);

											Set<String> dep = arg.get(fullyQualifiedClassName);
											if (classNames.contains(className)) {
												dep.add(className);
											}

											if (classNames.contains(className)) {
												ResolvedReferenceTypeDeclaration refType = combinedTypeSolver
														.solveType(className);
												if (refType.isInterface()) {
													System.out.println("It is an interface  " + className);
													// Find implementing this interface.

													// showReferenceTypeDeclaration(combinedTypeSolver.solveType(findImplentation
													// (className)));
													dep.add(findImplentation(className));
												} else {

													// showReferenceTypeDeclaration(combinedTypeSolver.solveType(className));
													dep.add(className);
												}
												for (int temp = 0; temp < methodDecl.getNumberOfParams(); temp++) {
													ResolvedParameterDeclaration param = methodDecl.getParam(temp);

													if (classNames.contains(param.describeType())) {
														dep.add(param.describeType());
														System.out.println(
																"Added dependent Param Type " + param.describeType());
													}

												}
												if (classNames.contains(methodDecl.getReturnType().describe())) {
													dep.add(methodDecl.getReturnType().describe());
													System.out.println ("Method return Type added" + methodDecl.getReturnType().describe());
												}
											} else {
												System.out.println(className
														+ " Need not be resolved as class not in scanned Package");
											}
										}
									} catch (UnsolvedSymbolException ex) {
										ex.printStackTrace();
										// throw ex;
									} catch (Exception ex) {
										System.out.println("Generic Exception " + ex.getLocalizedMessage());

										ex.printStackTrace();
									}
								} // visit method

							} // adaptor object creation
									.visit(method, collector);
						}); // each method for Each ends.

					}

				}.visit(getCompilationUnit( file , filePath), collectorPerClass);

				returnValue.putAll(collectorPerClass);

				Set<String> keySet = collectorPerClass.keySet();

				for (String key : keySet) {
					System.out
							.println(" In file " + file + " Key : " + key + "   Value : " + collectorPerClass.get(key));
				}

				System.out.println(); // empty line
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		).explore(new File (filePath));

		for (String key : returnValue.keySet()) {
			System.out.println(" Key : " + key + "   Value : " + returnValue.get(key));
		}

		System.out.println(); // empty line

		return returnValue;

	}

	private String findImplentation(String interFaceName) {

		return interfaceImplMap.get(interFaceName);
	}

	@SuppressWarnings("deprecation")
	public CompilationUnit getCompilationUnit( File file , String filePath ) throws FileNotFoundException {

		CombinedTypeSolver combinedTypeSolver = getCombinedTypeSolver(file, filePath);

		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

		final ParserConfiguration config = new ParserConfiguration().setStoreTokens(true)
				.setSymbolResolver(symbolSolver);

		StaticJavaParser.setConfiguration(config);
		return StaticJavaParser.parse(file);

	}

	public CombinedTypeSolver getCombinedTypeSolver(File file , String filePath) {
		TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
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

	private void showReferenceTypeDeclaration(ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration) {

		System.out.println(String.format("== %s ==", resolvedReferenceTypeDeclaration.getQualifiedName()));
		System.out.println(" fields:");
		resolvedReferenceTypeDeclaration.getAllFields()
				.forEach(f -> System.out.println(String.format("    %s %s", f.getType(), f.getName())));
		System.out.println(" methods:");
		resolvedReferenceTypeDeclaration.getAllMethods()
				.forEach(m -> System.out.println(String.format("    %s", m.getQualifiedSignature())));
		System.out.println();
	}
	
	
public static void load (String filePath) {
		
		HashMap<String, ArrayList<String>> extractdata = new HashMap<>();
		File projectDir = new File(filePath);
		ClassDependencies classDepencies = new ClassDependencies();
		mapClassAndDependencies = classDepencies.listDependantClassesInaFolder( filePath);

		Set<String> keySet = mapClassAndDependencies.keySet();
		for (String key : keySet) {
			System.out.println("Key : " + key + "   Value : " + mapClassAndDependencies.get(key));

		}

		for (String key : mapClassAndDependencies.keySet()) { //
			for (String controllerKey : listOfControllers)
				if (key.contains(controllerKey)) {
					mapControllerAndDependencies.put(key, new HashSet<String>());
					classDepencies.recursiveCall(key, mapClassAndDependencies.get(key));
				}
		}
		Set<String> keySetControllers = mapControllerAndDependencies.keySet();
		//String methodName = args[1].split("-")[0];

		for (String key : keySetControllers) {
			
			
				System.out.println("Controller Key : " + key + " Full Dependecies  Value : "
						+ mapControllerAndDependencies.get(key));
				
		}

		
		// mapControllerAndDependencies - has full dependencies for each controller.
	}

	

	public static void main(String[] args) 
	{
		
				extractedMain(JarFolders.SRC_FOLDER);
	}

	private static void extractedMain(final String filePath) {
		//args[0];
		getCall(filePath);
		
		File projectDir = new File(filePath);
		Set<String> keySetControllers = mapControllerAndDependencies.keySet();
		System.out.println ("keySetControllers" + keySetControllers);
		for (String key : keySetControllers)
			System.out.print("Key " + key + ", Value " + mapClassAndDependencies.get(key));
		
	}

	public static void extractController(String className , String methodName ) {
		HashMap<String, ArrayList<String>> extractdata = new HashMap<>();
		System.out.println(className);
		System.out.println(methodName);
		mapControllerAndDependencies = new HashMap<String, Set<String>>();
		Set<String> keySetControllers = mapControllerAndDependencies.keySet();
		String methodName1 = methodName.split("-")[0];
		extractdata.put(className, new ArrayList<String>());

		for (String key : keySetControllers) {
			if (key.contains(className)) {
				System.out.println("Controller Key : " + key + " Full Dependecies  Value : "
						+ mapControllerAndDependencies.get(key));
				if (!extractdata.get(className).contains(mapControllerAndDependencies.get(key))) {
					// extractdata.put(methodName.split(" ")[0],new ArrayList<String>());
					extractdata.get(className).addAll(mapControllerAndDependencies.get(key));

				}
			}
		}

		try {

			try {
				CopyFile.copyFile(extractdata, methodName1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public  void recursiveCall(final String controller, Set<String> dependencies) {

		// if (dependencies.size() == 0) return; --implicit

		if (dependencies.size() > 0) {
			Set<String> fullDependencies = mapControllerAndDependencies.get(controller);
			if (fullDependencies.addAll(dependencies)) // some elements are added.
				dependencies.stream().forEach(action -> recursiveCall(controller, mapClassAndDependencies.get(action)));
			// else return; --implicit
		}
	}

	

//	public  String filePath ; // TODO: Change to location of source parse here.
public static Map<String, String> interfaceImplMap = null;
	//public static Set<String> classNames = null;
	public static Map<String, String> classNameAndLocation = null;
	public static Map<String, Set<String>> mapClassAndDependencies = null;
	public static Map<String, Set<String>> mapControllerAndDependencies = null;
	public static Set<String> listOfControllers = null;
	// public static Map<String, Set<String>> mapClassAndDependencies = null;

	// public static Map<String, Set<String>> classNameAndDependants = null;
	public static void getCall(String filePath) {
		classNameAndLocation = ListInterface.getlistOfClassOrInterface( filePath); //Get List of classes in the source file system.
		//classNames = classNameAndLocation.keySet();
		interfaceImplMap = ListInterface.listImplementation( classNameAndLocation.keySet(), filePath); //Get all interfaces & its implementation
		listOfControllers = ListMethods.getClassesWithMethodNames( filePath).keySet(); //Get list of front Controllers.
		mapControllerAndDependencies = new HashMap<String, Set<String>>();
		load (filePath); // Get one level of dependencies for a all classes in file system.
	}
}
