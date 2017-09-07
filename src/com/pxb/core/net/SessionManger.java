/**    
* @Title: SessionManger.java  
* @Package com.pxb.core.net  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��7��28�� ����9:10:08  
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
 * @Description: �û�session���� 
 * @author panxiaobo  
 * @date 2017��7��28�� ����9:10:08  
 *    
 */
public class SessionManger implements UserUnRegListener,UserRegListener{
	
	private static AtomicInteger userId=new AtomicInteger(1);
	
	/**
	 * ��ȡuserId,�˴�ֻ�Ǿ������㣬ʵ����ĿӦ������Ŀҵ������
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
