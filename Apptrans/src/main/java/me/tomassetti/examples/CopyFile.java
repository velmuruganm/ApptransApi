package me.tomassetti.examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.Git;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class CopyFile {
	
	public static void copyFile(HashMap<String, ArrayList<String>> extractdata, String methodName ) throws IOException, InterruptedException{
        final String targetFolder = "D:\\Shopeasy_Copy";    //destination folder for pasting
        final String readFolder = "D:\\github\\ShoppingCart\\src\\main\\java\\com"; //source folder path
        final String finalMethodName = methodName;
        final String targetdir = targetFolder+"\\"+finalMethodName;
        final String javaproject =targetdir+"\\"+finalMethodName + "\\src\\main\\java\\com";
        List<File> fileList = listAllFiles(new File(readFolder));  // getting list of files with .java in their name
        //adding predicate filter for operation
        File file = new File(targetFolder+"\\"+finalMethodName);
       
//        File file1 = new File(targetFolder+"\\"+finalMethodName+\\+"com");
        if(!file.exists()) {
        file.mkdir(); 
        try {

			createproject(methodName);
			TimeUnit.SECONDS.sleep(45);
			
			
		} catch (CoreException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//        deletedir(methodName);
//        TimeUnit.SECONDS.sleep(10);
        clonedir(methodName);
        TimeUnit.SECONDS.sleep(10);
        FileUtils.deleteDirectory(new File(javaproject+"\\mycompany"));
       
        for(String key: extractdata.keySet()){
        for (String c1 :extractdata.get(key)){//converting className into path pattern
        fileList.forEach(e->{
        	
        	if(e.getPath().contains(key.replace(".","\\"))) {
        	
        		 try {
        			 
        		   if(e.getName().contains("Controller"))
        			   Files.copy(Paths.get(e.getPath()),Paths.get(javaproject+"\\controller\\"+e.getName()));
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                 
                 System.out.println("copied "+e.getName()+" Path:"+e.getPath());
        	}
        	
                    if(e.getPath().contains(c1.replace(".","\\"))){
                    	try {
                            //copying file here
                    		if (e.getName().contains("Dao")) {
        						Files.copy(Paths.get(e.getPath()),Paths.get(javaproject+"\\dao\\"+e.getName()));
                            				
                    		}
        			        if (e.getName().contains("Service")) { 
        			        	Files.copy(Paths.get(e.getPath()),Paths.get(javaproject+"\\service\\"+e.getName()));
        			        }
        			        if (!e.getName().contains("Service") && !e.getName().contains("Dao") && !e.getName().contains("Controller")) 
        			        	Files.copy(Paths.get(e.getPath()),Paths.get(javaproject+"\\model\\"+e.getName())); 
                            
                       
                            System.out.println("copied "+e.getName()+" Path:"+e.getPath());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                });
        
        }
        }
        }
        try {
        	TimeUnit.SECONDS.sleep(45);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        createRepo(methodName);
        }
	
	
	public static void clonedir(String methodName) throws IOException {
		final String targetFolder = "D:\\Shopeasy_Copy";    //destination folder for pasting
        final String readFolder = "D:\\github\\ShoppingCart\\src\\main\\java\\com"; //source folder path
        final String targetdir = targetFolder+"\\"+methodName;
        final String javaproject =targetdir+"\\"+methodName + "\\src\\main\\java\\com";
        List<File> fileList = listAllFiles(new File(readFolder));  // getting list of files with .java in their name
        //adding predicate filter for operation
        
       
//        File file1 = new File(targetFolder+"\\"+finalMethodName+\\+"com");
        
        
		Runtime.getRuntime().exec("cmd /c start cmd.exe /K \" cd .. && cd .. && c: && cd Users\\44976 && xcopy D:\\github\\ShoppingCart\\src\\main\\java\\com "+ javaproject + "/t /e" ); 

	}
	
	
	
	
	 public static void createproject(String methodName) throws CoreException {
	    	try
	        {  
	    		
	    		methodName = methodName.split("\\(")[0];
	    		final String javaproject = methodName + "\\src\\main\\java\\com\\mycompany\\app";
	    		Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"cd .. && cd .. && cd Shopeasy_Copy && cd "+methodName+"&& mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId="+methodName+" -DarchetypeVersion=1.4 -DinteractiveMode=false &&  cd "+javaproject ); 
	        } 
	        catch (Exception e) 
	        { 
	            System.out.println("HEY Buddy ! U r Doing Something Wrong "); 
	            e.printStackTrace(); 
	        } 
	    }
	
	public static List<File> listAllFiles(final File folder) throws IOException {
		// using Files.walk for getting all file and filtering based on predicate, look
		// predicate topic on net if it confuses you
		List<File> retVal = Files.walk(Paths.get(folder.getPath())).filter(Files::isRegularFile).map(Path::toFile)
				.filter(e -> e.getName().contains(".java")).collect(Collectors.toList());
		retVal.forEach(action -> {
			System.out.println("Identified java file for copy" + action.getAbsolutePath());
		});
		return retVal;

	}
	
	 public static void createRepo(String methodname) {
	    	try
	        {  
	    		methodname = methodname.split("\\(")[0];   		
	        	final String targetFolder = "D:\\Shopeasy_Copy";
	        	final String localPath = targetFolder+"\\"+methodname;
	        	File file = new File(localPath);
	        	Git git = Git.init().setDirectory(file).call();
	            System.out.println("Created repository: " + git.getRepository().getDirectory());
	            File myFile = new File(git.getRepository().getDirectory().getParent(), "testfile");
	            if (!myFile.createNewFile()) {
	                throw new IOException("Could not create file " + myFile);
	            }
	    
	        GitHub github = new GitHubBuilder().withPassword("PriyankaN2", "Priya#123!").build();
	    	GHRepository repo = github.createRepository(
	    	  methodname,"this is my new repository",
	    	  "https://www.kohsuke.org/",true/*public*/);
	    		
	    		Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"cd .. && cd .. && cd Shopeasy_Copy && cd "+methodname+"&& git init &&  git add . && git commit -m "+methodname+" && git remote add origin https://github.com/PriyankaN2/"+methodname+".git && git push origin master"); 
	        } 
	        catch (Exception e) 
	        { 
	            System.out.println("HEY Buddy ! U r Doing Something Wrong "); 
	            e.printStackTrace(); 
	        } 
	    	
	    }


}
