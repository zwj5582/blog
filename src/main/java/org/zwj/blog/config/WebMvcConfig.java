/*
 *  Created by ZhongWenjie on 2019-01-13 17:57
 */

package org.zwj.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zwj.blog.utils.Util;

import javax.servlet.ServletContext;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${http.server.host}")
    private String httpServerHost;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/admin/login").setViewName("admin/login");
        registry.addViewController("/admin/upload").setViewName("admin/upload/upload");
    }

    @Bean
    public ServletContext getServletContext(ServletContext servletContext, ServerProperties serverProperties) {
        if (Util.valid(httpServerHost))
            servletContext.setAttribute("httpServerHost", httpServerHost);
        else {
            
        }
        return servletContext;
    }




}
