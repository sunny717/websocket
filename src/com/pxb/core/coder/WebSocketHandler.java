/**    
* @Title: WebSocketHandler.java  
* @Package com.pxb.coder  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月27日 上午10:32:17  
* @version V1.0.0    
*/  
package com.pxb.core.coder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pxb.core.domain.WebSocketCodecPacket;

/**  
 * @ClassName: WebSocketHandler  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author panxiaobo  
 * @date 2017年7月27日 上午10:32:17  
 *    
 */
public class WebSocketHandler extends IoHandlerAdapter {
	Logger log=LoggerFactory.getLogger(WebSocketHandler.class);
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		String msg=session.getRemoteAddress()+"加入";
		super.sessionOpened(session);
		log.info(msg);
		session.write(WebSocketCodecPacket.buildPacket(IoBuffer.wrap(msg.getBytes("utf-8"))));
		
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		log.info(session.getRemoteAddress()+",close");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		IoBuffer buffer = (IoBuffer) message;		
		String theMessage = new String(buffer.array(),"utf-8");
		log.info("recieve msg:"+theMessage);
		session.write(WebSocketCodecPacket.buildPacket(IoBuffer.wrap(theMessage.getBytes("utf-8"))));
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	}
	
	
	

}
