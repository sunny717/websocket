/**    
* @Title: UserRegListener.java  
* @Package com.pxb.core  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月28日 上午9:17:38  
* @version V1.0.0    
*/  
package com.pxb.core.listener;

import com.pxb.core.net.UserSession;

/**  
 * @ClassName: UserRegListener  
 * @Description: 玩家注销监听器 
 * @author panxiaobo  
 * @date 2017年7月28日 上午9:17:38  
 *    
 */
public interface UserUnRegListener {
	
	void unReg(UserSession userSession);

}
