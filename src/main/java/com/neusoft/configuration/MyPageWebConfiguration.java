package com.neusoft.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.neusoft.interceptor.LoginRequiredInterceptor;
import com.neusoft.interceptor.PassportInterceptor;

@Component
public class MyPageWebConfiguration extends WebMvcConfigurerAdapter {
	@Autowired
	private PassportInterceptor passportInterceptor;

	@Autowired
	private LoginRequiredInterceptor loginRequiredInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(passportInterceptor);
		registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*");
		super.addInterceptors(registry);
	}

}
