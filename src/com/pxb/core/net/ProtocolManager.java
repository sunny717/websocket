/**    
* @Title: ProtocolManager.java  
* @Package com.pxb.core.net  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月28日 上午9:32:06  
* @version V1.0.0    
*/  
package com.pxb.core.net;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pxb.core.domain.CmdDefine;

/**  
 * @ClassName: ProtocolManager  
 * @Description: 协议管理类  
 * @author panxiaobo  
 * @date 2017年7月28日 上午9:32:06  
 *    
 */
public class ProtocolManager {
	
	private Map<Integer,CmdDefine> map;
	public static Logger log = LoggerFactory.getLogger(ProtocolManager.class);
	
	public void init(){
		Class[] paramType={Map.class,UserSession.class};
		for(CmdDefine cmdDefine:map.values()){
			Method method;
			try {
				method = cmdDefine.getBean().getClass().
						getMethod(cmdDefine.getMethodName(), paramType);
				cmdDefine.setMethod(method);
			} catch (Exception e) {
				log.error("ProtocolManager init error...",e);
			} 
			
		}
	}

	public Map<Integer, CmdDefine> getMap() {
		return map;
	}

	public void setMap(Map<Integer, CmdDefine> map) {
		this.map = map;
	}
	

}
