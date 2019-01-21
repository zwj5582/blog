/*
 *  Created by ZhongWenjie on 2019-01-13 17:57
 */

package org.zwj.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zwj.blog.utils.Util;

import javax.servlet.ServletContext;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/admin/login").setViewName("admin/login");
        registry.addViewController("/admin").setViewName("admin/admin");
        registry.addViewController("/admin/").setViewName("admin/admin");
        registry.addViewController("/admin/upload").setViewName("admin/upload/upload");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //添加映射路径
        registry.addMapping("/**")
                //放行哪些原始域
                .allowedOrigins("*")
                //是否发送Cookie信息
                .allowCredentials(true)
                //放行哪些原始域(请求方式)
                .allowedMethods("GET","POST", "PUT", "DELETE")
                //放行哪些原始域(头部信息)
                .allowedHeaders("*");
    }

    @Autowired
    public ServletContext getServletContext(
            ServletContext servletContext,
            ServerProperties serverProperties,
            @Value("${http.server.host}") String httpServerHost) {
        if (!Util.valid(httpServerHost)) {
            String address =
                    (Util.valid(serverProperties.getAddress()))
                            ? serverProperties.getAddress().getHostAddress()
                            : "127.0.0.1";
            Integer port =
                    (Util.valid(serverProperties.getPort())) ? serverProperties.getPort() : 8080;
            String protocolHeader =
                    (Util.valid(serverProperties.getSsl()) && serverProperties.getSsl().isEnabled())
                            ? "https://"
                            : "http://";
            String contextPath =
                    (Util.valid(serverProperties.getServlet().getContextPath()))
                            ? serverProperties.getServlet().getContextPath()
                            : "";
            httpServerHost =
                    protocolHeader
                            + address
                            + ":"
                            + port
                            + ((Util.valid(contextPath)) ? "/" + contextPath : "");
        }
        servletContext.setAttribute("httpServerHost", httpServerHost);
        return servletContext;
    }
}
