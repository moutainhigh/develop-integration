package org.cisiondata.modules.auth.web;

import org.apache.commons.lang.StringUtils;
import org.cisiondata.modules.auth.Constants.CookieName;
import org.cisiondata.modules.auth.Constants.SessionName;
import org.cisiondata.modules.auth.entity.User;
import org.cisiondata.modules.auth.service.IUserService;
import org.cisiondata.modules.auth.web.session.SessionManager;
import org.cisiondata.utils.redis.RedisClusterUtils;
import org.cisiondata.utils.spring.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(WebUtils.class);
	
	public static String getCurrentAccout() {
		User user = getCurrentUser();
		return null != user ? user.getAccount() : null;
	}

	public static User getCurrentUser() {
		WebContext webContext = WebContext.get();
		User user = null;
		if (null != webContext && null != webContext.getSession()) {
			user = webContext.getSession().getAttribute(SessionName.CURRENT_USER);
		} 
		if (null == user) {
//			String sessionId = getCookieValueFromHead(CookieName.USER_SESSION);
			try {
				String sessionId = (String) RedisClusterUtils.getInstance().get(getAccessTokenFromHead());
				LOG.info("WebUtils sessionId: {}", sessionId);
				if (StringUtils.isBlank(sessionId)) return null;
				SessionManager sessionManager = SpringBeanFactory.getBean(SessionManager.class);
				Object accountObject = sessionManager.getStorageHandler().getAttribute(sessionId, 
						webContext.getRequest(), webContext.getResponse(), SessionName.CURRENT_USER_ACCOUNT);
				LOG.info("WebUtils account: {}", accountObject);
				Object userObject = sessionManager.getStorageHandler().getAttribute(sessionId, 
						webContext.getRequest(), webContext.getResponse(), SessionName.CURRENT_USER);
				LOG.info("WebUtils user: {}", userObject);
				if (null != accountObject) {
					IUserService userService = SpringBeanFactory.getBean(IUserService.class);
					user = userService.readUserByAccount((String) accountObject);
					setCurrentUser(user);
					return user;
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return user;
	}
	
	public static void setCurrentUser(User user) {
		WebContext webContext = WebContext.get();
		if (null != webContext && null != webContext.getSession()) {
			webContext.getSession().setAttribute(SessionName.CURRENT_USER, user);
		}
	}
	
	public static void removeCurrentUser() {
		WebContext webContext = WebContext.get();
		if (null != webContext && null != webContext.getSession()) {
			webContext.getSession().removeAttribute(SessionName.CURRENT_USER);
		}
	}
	
	public static String getAccessTokenFromHead() {
		return WebContext.get().getRequest().getHeader("accessToken");
	}
	
	public static String getAccountFromHead() {
		return getCookieValueFromHead(CookieName.USER_ACCOUNT);
	}
	
	public static String getCookieValueFromHead(String cookieKey) {
		String cookieValue = "";
		String cookieInfo = WebContext.get().getRequest().getHeader("Cookie");
		String[] cookies = cookieInfo.split(";");
		for (int i = 0, len = cookies.length; i < len; i++) {
			String cookie = cookies[i];
			if (cookie.indexOf("=") != -1) {
				String[] cookieKV = cookie.split("=");
				if (cookieKey.equals(cookieKV[0].trim())) {
					cookieValue = cookieKV.length == 2 ? cookieKV[1] : "";
				}
			}
		}
		return cookieValue;
	}
	
}
