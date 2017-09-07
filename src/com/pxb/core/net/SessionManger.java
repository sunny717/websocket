/**    
* @Title: SessionManger.java  
* @Package com.pxb.core.net  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月28日 上午9:10:08  
* @version V1.0.0    
*/  
package com.pxb.core.net;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.pxb.core.listener.UserRegListener;
import com.pxb.core.listener.UserUnRegListener;
import com.pxb.core.util.CollectionUtil;

/**  
 * @ClassName: SessionManger  
 * @Description: 用户session管理 
 * @author panxiaobo  
 * @date 2017年7月28日 上午9:10:08  
 *    
 */
public class SessionManger implements UserUnRegListener,UserRegListener{
	
	private static AtomicInteger userId=new AtomicInteger(1);
	
	/**
	 * 获取userId,此处只是举例方便，实际项目应该有项目业务生成
	 * @return
	 */
	public static int getUserId(){
		return userId.getAndIncrement();
	}
	private Map<Integer,UserSession> userList=CollectionUtil.newConcurrentHashMap();

	@Override
	public void reg(UserSession userSession) {
		
		userList.put(userSession.getUserId(),userSession);
	}

	@Override
	public void unReg(UserSession userSession) {
		userList.remove(userSession.getUserId());
	}

}
