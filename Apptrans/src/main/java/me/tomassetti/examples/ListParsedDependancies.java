package me.tomassetti.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class ListParsedDependancies {
	static List<String> listOfControllerClasses = new ArrayList<String>();

	public static void listClasses(File projectDir , String filePath) {
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
						// showReferenceTypeDeclaration(combinedTypeSolver1.solveType
						// (n.getNameAsString()));

						n.findAll(FieldDeclaration.class).forEach(field -> {
							field.getVariables().forEach(variable -> {
								// Print the field's class typr
								System.out.println("Variable Type :=>" + variable.getType());
								// System.out.println("Full Type Useful for us :=>" + javaParserFacade.solve
								// (variable.getType().asString()));
								// Print the field's name
								System.out.println("Variable Name :=>" + variable.getName());
								// Print the field's init value, if not null
								variable.getInitializer()
										.ifPresent(initValue -> System.out.println(initValue.toString()));
							});
						});

						n.getMethods().forEach(method -> {
							System.out.println("Method name : " + method.getName());
							System.out.println("Method type : " + method.getType());

							new VoidVisitorAdapter<Object>() {
								public void visit(MethodCallExpr mce, Object arg) {
									super.visit(mce, arg);
									System.out.println(" [L " + mce.getBegin().get().line + "] " + mce);
									try {

										CombinedTypeSolver combinedTypeSolver = getCombinedTypeSolver(filePath);
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

											
											
											if (classNames.contains(className))
											{
												ResolvedReferenceTypeDeclaration refType = combinedTypeSolver
														.solveType(className);
												if (refType.isInterface()) {
													System.out.println("It is an interface  " + className);
													// Find implementing this interface.
													
													showReferenceTypeDeclaration(combinedTypeSolver.solveType(findImplentation (className)));	
												} else {
	
													showReferenceTypeDeclaration(combinedTypeSolver.solveType(className));
												}
												for (int temp = 0; temp < methodDecl.getNumberOfParams(); temp++) {
													ResolvedParameterDeclaration param = methodDecl.getParam(temp);
													System.out.println("Param Type " + param.describeType());
	
												}
											}
											else {
												System.out
												.println(className + " Need not be resolved as class not in scanned Package");
											}
										}
									} catch (UnsolvedSymbolException ex) {
										ex.printStackTrace();
										// throw ex;
									} catch (Exception ex) {
										System.out.println("Generic Exception " + ex.getLocalizedMessage());

										// ex.printStackTrace();
									}
								} // visit method

							} // adaptor object creation
									.visit(method, arg);
						}); // each method for Each ends.

					}

				}.visit(getCompilationUnit(file , filePath), null);
				System.out.println(); // empty line
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		).explore(projectDir);

	}

	private static String findImplentation(String interFaceName) {

		return interfaceImplMap.get(interFaceName);
	}

	@SuppressWarnings("deprecation")
	public static CompilationUnit getCompilationUnit(File file , String filePath) throws FileNotFoundException {

		CombinedTypeSolver combinedTypeSolver = getCombinedTypeSolver(filePath);

		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

		final ParserConfiguration config = new ParserConfiguration().setStoreTokens(true)
				.setSymbolResolver(symbolSolver);

		StaticJavaParser.setConfiguration(config);
		return StaticJavaParser.parse(file);

	}

	public static CombinedTypeSolver getCombinedTypeSolver(String filePath) {
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

	private static void showReferenceTypeDeclaration(
			ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration) {

		System.out.println(String.format("== %s ==", resolvedReferenceTypeDeclaration.getQualifiedName()));
		System.out.println(" fields:");
		resolvedReferenceTypeDeclaration.getAllFields()
				.forEach(f -> System.out.println(String.format("    %s %s", f.getType(), f.getName())));
		System.out.println(" methods:");
		resolvedReferenceTypeDeclaration.getAllMethods()
				.forEach(m -> System.out.println(String.format("    %s", m.getQualifiedSignature())));
		System.out.println();
	}

	public static void main(String[] args) {
		
				
				extractedMain(JarFolders.SRC_FOLDER);
		
	}

	public static void extractedMain(final String filePath) {
		//final String filePath = args[0];
		File projectDir = new File(filePath);
		file = projectDir;
		getstart(filePath);
		// new
		// File("D:/github/analyze-java-code-examples/src/main/java/me/tomassetti/examples");

		listClasses(projectDir , filePath);
		// listMethodCalls(projectDir);
	}

	

//	public final static String filePath = "source_to_parse/ShoppingCart-master/src"; // TODO: Change to location of source parse here.

	public static File file = null;

	public static Map<String, String> interfaceImplMap = null;
	public static Set<String> classNames = null;
	public static Map<String, String>  classNameAndLocation = null;
	public static void getstart(String filePath) {
		classNameAndLocation = ListInterface.getlistOfClassOrInterface( filePath);
		classNames = classNameAndLocation.keySet();
		interfaceImplMap = ListInterface.listImplementation( classNames , filePath);
	}
}
