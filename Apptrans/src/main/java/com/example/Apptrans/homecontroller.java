package com.example.Apptrans;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RestController;

import me.tomassetti.examples.ClassDependencies;




@RestController
public class homecontroller {
	
	

	@GetMapping(path="/{className}/{methodName}")
	public String getClassAndMethod (@PathVariable("className")String className,@PathVariable("methodName")String methodName) throws Exception

	{
			
		ClassDependencies.main(new String[]{ className ,methodName});
  	    
		return "Class Name :"+className+"  "+"Method Name :"+methodName;

		
		
	}
}


