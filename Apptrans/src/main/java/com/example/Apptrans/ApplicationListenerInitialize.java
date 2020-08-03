package com.example.Apptrans;


import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import me.tomassetti.examples.ClassDependencies;

@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent>  {
     
    //private static final Logger logger = LoggerFactory.getLogger(ApplicationListenerInitialize.class);
 
    public void onApplicationEvent(ApplicationReadyEvent event) {
       System.out.println ("I waited until Spring Boot finished before getting here!");
       ClassDependencies dep = new ClassDependencies(); //load static blocks.
       System.out.println ("Spring Boot Loaded dependencies");
    }
}