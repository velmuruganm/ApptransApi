
package me.tomassetti.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import me.tomassetti.support.DirExplorer;

public class ListInterface {

	public static Map<String, String> listImplementation(File projectDir, Set<String>  classNames , String filePath) {
		
		System.out.println ("Scanning for Interface & its implementation Starts#");
		Map<String, String> retVal = new HashMap<String, String>();
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
			
			try {

				VoidVisitor<Map<String, String>> implNameCollector = new ImplNameCollector(classNames);
				implNameCollector.visit(ListParsedDependancies.getCompilationUnit(file, filePath), retVal);
				// methodNames.forEach(n -> retVal.add(n));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).explore(projectDir);
		System.out.println ("Scanning for Interface & its implementation Ends#");
		return retVal;
	}

	public  static Map<String, String> getlistOfClassOrInterface(File projectDir , String filePath ) {
		Set<String>  classNames = new HashSet<String>();
		Map<String, String> mapClassNames = new HashMap<String,String > ();
		List<String> classesInaFile = new ArrayList <String>();
		System.out.println ("Scanning for Classes or Interface Starts#");
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
			
			try {

				VoidVisitor<Set<String>> classNameCollector = new ClassNameCollector();
				VoidVisitor<List<String>> classMapCollector = new ClassNameMapCollector();
				classNameCollector.visit(ListParsedDependancies.getCompilationUnit(file, filePath), classNames);
				classMapCollector.visit(ListParsedDependancies.getCompilationUnit(file , filePath), classesInaFile);
				// methodNames.forEach(n -> retVal.add(n));
				classesInaFile.stream().forEach(item -> mapClassNames.put(item, path));
				classesInaFile.clear();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).explore(projectDir);
		System.out.println ("Scanning for Classes or Interface Ends#");
		
		
		Iterator<String> itr = mapClassNames.keySet().iterator();
		while (itr.hasNext()) {
			String  key = itr.next();
			System.out.println("ClassName:> "  + key + " #FileLocation:> " + mapClassNames.get(key));
			
		}
		
		
		System.out.println ("printing  for Classes and it location Ends#");
		return mapClassNames;
	}

	
	

	
	public static void main(String[] args) {
		final String filePath = args[0];
		File projectDir = new File(args[0]);
		Map<String, String> classNames = getlistOfClassOrInterface(projectDir , filePath);
		
		Map<String, String> map = listImplementation(projectDir, classNames.keySet(), filePath);
		map.forEach( (key, val) -> 
		{
			System.out.println ("Collected key " + key + " and Value " + val);
		});
	}

//	public final static String filePath = "SampleSrcToParse/src";

	private static class ImplNameCollector extends VoidVisitorAdapter<Map<String, String>> {

		
		Set<String> classNames = null;
		ImplNameCollector (Set<String> className)
		{
			this.classNames = className;
		}
		@Override
		public void visit(ClassOrInterfaceDeclaration interfaceOrClass, Map<String, String> collector) {
			super.visit(interfaceOrClass, collector);
			String className =  interfaceOrClass.resolve().getQualifiedName();
			if (interfaceOrClass.isInterface()) {

				System.out.println("Interface  encountered  is => " + className);

			} else {
				
				
				NodeList<ClassOrInterfaceType> interfaceList = interfaceOrClass.getImplementedTypes();
				interfaceList.stream().forEach(a -> {
					
					String interfaceName  = a.resolve().getQualifiedName();
				
					
					if (this.classNames.contains(interfaceName))
					{
						System.out.println("Interface =>" + interfaceName);

						System.out.println("Implementation  =>" + className);
						collector.put(interfaceName, className);
					}
					else
						System.out.println("Interface Not found=>" + interfaceName);
						//jdk or dependendant classes are not being used.
					
				});

			}

		}
		
		
		

	}

	
	private static class ClassNameCollector extends VoidVisitorAdapter<Set<String>> {

		@Override
		public void visit(ClassOrInterfaceDeclaration interfaceOrClass, Set<String> collector) {
			super.visit(interfaceOrClass, collector);

			
				System.out.println("InterfaceOrClass encountered in parsing folder =>" + interfaceOrClass.resolve().getQualifiedName());
				collector.add(interfaceOrClass.resolve().getQualifiedName());
		
		}
	
	}

	
	private static class ClassNameMapCollector extends VoidVisitorAdapter<List<String>>  {

		@Override
		public void visit(ClassOrInterfaceDeclaration interfaceOrClass, List<String> collector) {
			super.visit(interfaceOrClass, collector);

			
				System.out.println("InterfaceOrClass encountered in parsing folder =>" + interfaceOrClass.resolve().getQualifiedName());
				collector.add(interfaceOrClass.resolve().getQualifiedName());
		
		}
	
	}
}
