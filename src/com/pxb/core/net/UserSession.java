/**    
* @Title: UserSession.java  
* @Package com.pxb.core.net  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月28日 上午9:07:18  
* @version V1.0.0    
*/  
package com.pxb.core.net;

import org.apache.mina.core.session.IoSession;

/**  
 * @ClassName: UserSession  
 * @Description: 用户session  
 * @author panxiaobo  
 * @date 2017年7月28日 上午9:07:18  
 *    
 */
public class UserSession {
	private IoSession session;  //mina,session对象
	
	public static final String USERID="userId";
	public static final String USERNAME="userName";
	
	public UserSession(IoSession session){
		this.session=session;
	}
	
	public int getUserId(){
		Object o=get(UserSession.USERID);
		return Integer.parseInt(o.toString());
	}
	
	public Object get(String key) {
		return session.getAttribute(key);
	}
	public void put(String key,Object o) {
		 session.setAttribute(key, o);
	}

}
