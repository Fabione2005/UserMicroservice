package com.ms.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ms.user.security.JWTAuthorizationFilter;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class UserMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}
	
	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
				.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/user/auth").permitAll()
				.antMatchers(HttpMethod.GET, "/v2/api-docs").permitAll()
				.anyRequest().authenticated();
		}
		
		//Esto es solo para que ustedes puedan agregar datos en la consola directamente si quieren
		@Override
	    public void configure(WebSecurity web) throws Exception {
	        web
	            .ignoring()
	            .antMatchers("/h2-console/**");
	    }
	}

}
