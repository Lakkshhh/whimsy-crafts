package com.eCommerceSite.WhimsyCrafts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;


@SpringBootApplication
@PropertySources({
		@PropertySource("classpath:/application.properties")
})
public class WhimsyCraftsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhimsyCraftsApplication.class, args);
	}

}
