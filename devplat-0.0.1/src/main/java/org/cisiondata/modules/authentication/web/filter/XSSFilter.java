package org.cisiondata.modules.authentication.web.filter;

import java.io.IOException;

import javax.servlet.Filter;  
import javax.servlet.FilterChain;  
import javax.servlet.FilterConfig;  
import javax.servlet.ServletException;  
import javax.servlet.ServletRequest;  
import javax.servlet.ServletResponse;  
import javax.servlet.http.HttpServletRequest;  

public class XSSFilter implements Filter {    
    
    @Override    
    public void init(FilterConfig filterConfig) throws ServletException {    
    }    
    
    @Override    
    public void doFilter(ServletRequest request, ServletResponse response,    
            FilterChain chain) throws IOException, ServletException {    
        XSSHttpServletRequestWrapper xssRequest = new XSSHttpServletRequestWrapper(    
                (HttpServletRequest) request);    
        chain.doFilter(xssRequest, response);    
    }    
    
    @Override    
    public void destroy() {    
    }    

}
