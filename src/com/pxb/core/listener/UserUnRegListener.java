/**    
* @Title: UserRegListener.java  
* @Package com.pxb.core  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��7��28�� ����9:17:38  
* @version V1.0.0    
*/  
package com.pxb.core.listener;

import com.pxb.core.net.UserSession;

/**  
 * @ClassName: UserRegListener  
 * @Description: ���ע�������� 
 * @author panxiaobo  
 * @date 2017��7��28�� ����9:17:38  
 *    
 */
public interface UserUnRegListener {
	
	void unReg(UserSession userSession);

}
