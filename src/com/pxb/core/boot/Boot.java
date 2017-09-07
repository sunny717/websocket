/**    
* @Title: Boot.java  
* @Package com.pxb.boot  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��7��27�� ����10:38:16  
* @version V1.0.0    
*/  
package com.pxb.core.boot;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pxb.core.server.SocketServer;

/**  
 * @ClassName: Boot  
 * @Description: ����������  
 * @author panxiaobo  
 * @date 2017��7��27�� ����10:38:16  
 *    
 */
public class Boot {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		// ����socket server
		final SocketServer webSocketServer = (SocketServer) context.getBean("webSocketServer");
		webSocketServer.start();
	}

}
