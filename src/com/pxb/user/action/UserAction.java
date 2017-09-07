/**    
 * @Title: UserAction.java  
 * @Package com.pxb.user.action  
 * @Description: TODO(��һ�仰�������ļ���ʲô)  
 * @author panxiaobo    
 * @date 2017��8��1�� ����9:31:10  
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
 * @Description: �û��ӿ�
 * @author panxiaobo
 * @date 2017��8��1�� ����9:31:10
 * 
 */
public class UserAction {
	private List<UserUnRegListener> userUnRegListeners;
	private List<UserRegListener> userRegListeners;

	/**
	 * ��¼����
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
	 * ע��
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
