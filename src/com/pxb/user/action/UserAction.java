/**    
 * @Title: UserAction.java  
 * @Package com.pxb.user.action  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年8月1日 上午9:31:10  
 * @version V1.0.0    
 */
package com.pxb.user.action;

import java.util.List;
import java.util.Map;

import com.pxb.core.listener.UserRegListener;
import com.pxb.core.listener.UserUnRegListener;
import com.pxb.core.net.SessionManger;
import com.pxb.core.net.UserSession;

/**
 * @ClassName: UserAction
 * @Description: 用户接口
 * @author panxiaobo
 * @date 2017年8月1日 上午9:31:10
 * 
 */
public class UserAction {
	private List<UserUnRegListener> userUnRegListeners;
	private List<UserRegListener> userRegListeners;

	/**
	 * 登录请求
	 * 
	 * @param userSession
	 * @param map
	 */
	public void login(UserSession userSession, Map map) {
		String userName = (String) map.get(UserSession.USERNAME);
		userSession.put(UserSession.USERNAME, userName);
		userSession.put(UserSession.USERID, SessionManger.getUserId());
		for(UserRegListener userRegListener:userRegListeners){
			userRegListener.reg(userSession);
		}
	}
	/**
	 * 注销
	 * @param userSession
	 * @param map
	 */
	public void unLogin(UserSession userSession, Map map) {
		for(UserUnRegListener userUnRegListener:userUnRegListeners){
			userUnRegListener.unReg(userSession);
		}
	}


	public void setUserUnRegListeners(List<UserUnRegListener> userUnRegListeners) {
		this.userUnRegListeners = userUnRegListeners;
	}

	public void setUserRegListeners(List<UserRegListener> userRegListeners) {
		this.userRegListeners = userRegListeners;
	}
	
	

}
