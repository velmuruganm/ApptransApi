package com.example.Apptrans;

import org.apache.tomcat.jni.Address;
import org.junit.Test;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.tomassetti.examples.BaseItem;
import me.tomassetti.examples.ClassDependencies;
import me.tomassetti.examples.ListInterface;

import javax.ws.rs.POST;
import javax.ws.rs.GET;

// import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import me.tomassetti.examples.ListMethods;
import me.tomassetti.examples.ParentItem;
import flexjson.JSONSerializer;


@RestController

public class homecontroller  {
	

	
	@GetMapping(path = "/")
	public String imUpAndRunning() {
		return "{healthy:true}";
	}
	
	
	@GetMapping(path = "/items-result/{srcfolder}")
	@CrossOrigin(origins = "http://localhost:3000")
    @Produces(MediaType.APPLICATION_JSON)
    public String getanalyser(@PathVariable("srcfolder")String srcfolder)throws Exception
    {
		ListMethods.main(new String[]{ srcfolder.replace(".","\\") });
		ParentItem results = ListMethods.sourceanalyser(srcfolder.replace(".","\\"));
		ClassDependencies.main(new String[] {srcfolder.replace(".","\\") });
		ListInterface.main(new String[] {srcfolder.replace(".","\\") });
		Gson gson = new Gson();
		String json = gson.toJson(results.getItems());
	      return json;
    }
	
	@GetMapping(path = "/map-result/{srcFolder}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<String>> getControllers(@PathVariable("srcfolder")String srcfolder)throws Exception
    {
		
		ListMethods list = new ListMethods();
		Map<String, List<String>> results = list.getControllers(srcfolder);
		JSONSerializer serializer = new JSONSerializer().prettyPrint(true); // pretty print JSON
		String jsonStr = serializer.serialize(results);
	      return results;
//        String output = JsonConvert.SerializeObject(results);
    }
	

	@GetMapping(path="/{className}/{methodName}")
	public String getClassAndMethod (@PathVariable("className")String className,@PathVariable("methodName")String methodName) throws Exception
	{	
		ClassDependencies.main(new String[]{ className ,methodName});
  	    return "Class Name :"+className+"  "+"Method Name :"+methodName;

	}

}





