/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pxb.core.coder;

import java.io.UnsupportedEncodingException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pxb.core.domain.WebSocketCodecPacket;
import com.pxb.core.domain.WebSocketHandShakeResponse;
import com.pxb.core.util.WebSocketUtils;

/**
 * Encodes incoming buffers in a manner that makes the receiving client type transparent to the 
 * encoders further up in the filter chain. If the receiving client is a native client then
 * the buffer contents are simply passed through. If the receiving client is a websocket, it will encode
 * the buffer contents in to WebSocket DataFrame before passing it along the filter chain.
 * 
 * Note: you must wrap the IoBuffer you want to send around a WebSocketCodecPacket instance.
 * 
 * @author DHRUV CHOPRA
 */
public class WebSocketEncoder extends ProtocolEncoderAdapter{
	static Logger log=LoggerFactory.getLogger(WebSocketEncoder.class);

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        boolean isHandshakeResponse = message instanceof WebSocketHandShakeResponse;
        boolean isDataFramePacket = message instanceof WebSocketCodecPacket;
        boolean isRemoteWebSocket = session.containsAttribute(WebSocketUtils.SessionAttribute) && (true==(Boolean)session.getAttribute(WebSocketUtils.SessionAttribute));
        IoBuffer resultBuffer;
        if(isHandshakeResponse){
            WebSocketHandShakeResponse response = (WebSocketHandShakeResponse)message;
            resultBuffer = WebSocketEncoder.buildWSResponseBuffer(response);
        }
        else if(isDataFramePacket){
            WebSocketCodecPacket packet = (WebSocketCodecPacket)message;
            resultBuffer = isRemoteWebSocket ? WebSocketEncoder.buildWSDataFrameBuffer(packet.getPacket()) : packet.getPacket();
        }
        else{
            throw (new Exception("message not a websocket type"));
        }
        
        out.write(resultBuffer);
    }
    
    // Web Socket handshake response go as a plain string.
    private static IoBuffer buildWSResponseBuffer(WebSocketHandShakeResponse response) {                
        IoBuffer buffer = IoBuffer.allocate(response.getResponse().getBytes().length, false);
        buffer.setAutoExpand(true);
        buffer.put(response.getResponse().getBytes());
        buffer.flip();
        return buffer;
    }
    
    // Encode the in buffer according to the Section 5.2. RFC 6455
    private static IoBuffer buildWSDataFrameBuffer(IoBuffer buf) throws UnsupportedEncodingException {
    	log.info("send msg:"+new String(buf.array(),"utf-8"));
        IoBuffer buffer = IoBuffer.allocate(buf.limit() + 2, false);
        buffer.setAutoExpand(true);
        /*
         *%x0 表示连续消息分片
		 *%x1 表示文本消息分片
         *%x2 表未二进制消息分片
         *%x3-7 为将来的非控制消息片断保留的操作码
         *%x8 表示连接关闭  %x9 表示心跳检查的ping
         *%xA 表示心跳检查的pong
         *%xB-F 为将来的控制消息片断的保留操作码
         *此处使用0x82表示二进制消息分片
         */
        buffer.put((byte) 0x82);
        if(buffer.capacity()<= 125){
            byte capacity = (byte) (buf.limit());
            buffer.put(capacity);
        }
        else if(buffer.capacity()<Short.MAX_VALUE){
            buffer.put((byte)126);
            buffer.putShort((short)buf.limit());
        }else{
        	buffer.put((byte)127);
            buffer.putLong((long)buf.limit());
        }
        buffer.put(buf);
        buffer.flip();
        return buffer;
    }
    
}
