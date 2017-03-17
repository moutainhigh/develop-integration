package org.cisiondata.modules.log.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.cisiondata.modules.log.dao.UserAccessLogDAO;
import org.cisiondata.modules.log.entity.UserAccessLog;
import org.cisiondata.utils.spring.SpringBeanFactory;
import org.cisiondata.utils.web.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AccessLogFilter implements Filter {
	
	private Logger LOG = LoggerFactory.getLogger("ACCESS_LOG");
	private UserAccessLogDAO userAccessLogDAO = null;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		userAccessLogDAO = SpringBeanFactory.getBean("userAccessLogDAO");
	}
	
	@Override
	public void destroy() {
	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String jsessionId = httpServletRequest.getRequestedSessionId();
        String ip = IPUtils.getIPAddress(httpServletRequest);
        String accept = httpServletRequest.getHeader("accept");
        String userAgent = httpServletRequest.getHeader("User-Agent");
        String url = httpServletRequest.getRequestURI();
        String params = getParams(httpServletRequest);
        String headers = getHeaders(httpServletRequest);
        StringBuilder sb = new StringBuilder();
        sb.append(getBlock(jsessionId));
        sb.append(getBlock(ip));
        sb.append(getBlock(accept));
        sb.append(getBlock(userAgent));
        sb.append(getBlock(url));
        sb.append(getBlock(params));
        sb.append(getBlock(headers));
        sb.append(getBlock(httpServletRequest.getHeader("Referer")));
        LOG.info(sb.toString());
        if(url.indexOf("/qq")!=-1 || url.indexOf("/search")!=-1 || url.indexOf("/phoneBank")!=-1 || url.indexOf("/query")!=-1 || url.indexOf("/mobile")!=-1){        	
        	//TODO
        	String account = "TODO";
        	if(url.indexOf("/devplat/mobile")!=-1){
        		String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$"; 
        		Pattern p = Pattern.compile(regExp); 
        		UserAccessLog logModel = new UserAccessLog();
    			String IP = getIPAddress(httpServletRequest);
    			String keyword = url.substring(url.lastIndexOf("/")+1, url.length());
//    			String account = (String) SecurityUtils.getSubject().getPrincipal();
    			Matcher m = p.matcher(keyword);
    			if(!IP.equals("0:0:0:0:0:0:0:1")){
    				logModel.setIp(IP);
    				logModel.setAccessTime(new Date());
    				logModel.setParams(keyword);
    				logModel.setAccount(account);
    				if(m.matches()){
    					userAccessLogDAO.addLog(logModel);
    				}
    			}
        	}else{
        		Gson gson = new Gson();
        		Map<String, Object> map = gson.fromJson(params, Map.class);
        		map.remove("dateline");
        		map.remove("searchToken");
        		map.remove("scrollId");
        		map.remove("rowNumPerPage");
        		map.remove("accessId");
        		map.remove("token");
        		map.remove("pageSize");
        		map.remove("esindex");
        		map.remove("estype");
        		for (Map.Entry<String, Object> entry: map.entrySet()) {
        			UserAccessLog logModel = new UserAccessLog();
        			String keyword = entry.getValue().toString();
        			String IP = getIPAddress(httpServletRequest);
//        			String account = (String) SecurityUtils.getSubject().getPrincipal();
        			if(!IP.equals("0:0:0:0:0:0:0:1")){
        				logModel.setIp(IP);
        				logModel.setAccessTime(new Date());
        				logModel.setParams(keyword.substring(1,keyword.length()-1));
        				logModel.setAccount(account);
        				userAccessLogDAO.addLog(logModel);
        			}
        		}
        	}
        }
        
        chain.doFilter(request, response);
	}
	//获取IP的方法
	public static String getIPAddress(HttpServletRequest request){
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("HTTP_XFORWARDED_FOR");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	 public static String getBlock(Object msg) {
	        if (msg == null) {
	            msg = "";
	        }
	        return "[" + msg.toString() + "]";
	    }

    @SuppressWarnings("unchecked")
	protected static String getParams(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return new GsonBuilder().serializeSpecialFloatingPointValues()
				.setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(params);
    }

	@SuppressWarnings("unchecked")
	private static String getHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = Maps.newHashMap();
        Enumeration<String> namesEnumeration = request.getHeaderNames();
        while(namesEnumeration.hasMoreElements()) {
            String name = namesEnumeration.nextElement();
            Enumeration<String> valueEnumeration = request.getHeaders(name);
            List<String> values = Lists.newArrayList();
            while(valueEnumeration.hasMoreElements()) {
                values.add(valueEnumeration.nextElement());
            }
            headers.put(name, values);
        }
        return new GsonBuilder().serializeSpecialFloatingPointValues()
				.setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(headers);
    }


}