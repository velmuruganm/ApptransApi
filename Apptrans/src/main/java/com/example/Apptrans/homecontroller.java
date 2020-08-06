package com.example.Apptrans;

import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.tomassetti.examples.BaseItem;
import me.tomassetti.examples.ClassDependencies;
import javax.ws.rs.POST;
import javax.ws.rs.GET;

// import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import me.tomassetti.examples.ListMethods;
import me.tomassetti.examples.ParentItem;
import flexjson.JSONSerializer;


@RestController
public class homecontroller {
	

	
	@GetMapping(path = "/")
	public String imUpAndRunning() {
		return "{healthy:true}";
	}
	
	@GetMapping(path = "/java")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BaseItem> getanalyser()
    {
		
		ListMethods list = new ListMethods();
		ParentItem results = list.sourceanalyser();
//		JSONSerializer serializer = new JSONSerializer().prettyPrint(true); // pretty print JSON
//		String jsonStr = serializer.serialize(results);
	      return results.getItems();
//        String output = JsonConvert.SerializeObject(results);
    }
	
	
	@GetMapping(path = "/listMethods")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<String>> getControllers()
    {
		
		ListMethods list = new ListMethods();
		Map<String, List<String>> results = list.getControllers();
		JSONSerializer serializer = new JSONSerializer().prettyPrint(true); // pretty print JSON
		String jsonStr = serializer.serialize(results);
	      return results;
//        String output = JsonConvert.SerializeObject(results);
    }
	
	
	
//	[HttpGet("getitems")]
//	        [Produces("application/json")]
//	        public JsonResult Get(String Appname)
//	        {
//	            SourceAnalyser sa = new SourceAnalyser();
//	            //SourceVBAnalyser sa = new SourceVBAnalyser();
//	            sa.AppName = Appname;
//	            sa.StartAnalyse();
//	            if (sa.getparent != null && sa.getparent.Items.Count() > 0)
//	            {
//	                return new JsonResult(JsonConvert.SerializeObject(sa.getparent.Items, Formatting.None));
//	            }
//	            else
//	            {
//	                return new JsonResult(null);
//	            }
//	        }
//	

	@GetMapping(path="/{className}/{methodName}")
	public String getClassAndMethod (@PathVariable("className")String className,@PathVariable("methodName")String methodName) throws Exception

	{
			
		ClassDependencies.main(new String[]{ className ,methodName});
  	    
		return "Class Name :"+className+"  "+"Method Name :"+methodName;

		
		
	}
	
//	@GetMapping(path="/startAnalyser")
//	public String getstartAnalyser ()
//	{
//			
//		ListMethods.main();
//  	    
//		return "Class Name :"+className+"  "+"Method Name :"+methodName;
//
//		
//		
//	}
	
	
}


