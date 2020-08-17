package com.example.Apptrans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class ApptransApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApptransApplication.class, args);
	}

	 @Bean
	    public WebMvcConfigurer configurer(){
	      return new WebMvcConfigurer(){
	        @Override
	        public void addCorsMappings(CorsRegistry registry) {
	          registry.addMapping("/*")
	              .allowedOrigins("http://localhost:3000");
	        }
	  };
	 }
}
