/**    
* @Title: Boot.java  
* @Package com.pxb.boot  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月27日 上午10:38:16  
* @version V1.0.0    
*/  
package com.pxb.core.boot;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pxb.core.server.SocketServer;

/**  
 * @ClassName: Boot  
 * @Description: 服务启动类  
 * @author panxiaobo  
 * @date 2017年7月27日 上午10:38:16  
 *    
 */
public class Boot {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		// 启动socket server
		final SocketServer webSocketServer = (SocketServer) context.getBean("webSocketServer");
		webSocketServer.start();
	}

}
