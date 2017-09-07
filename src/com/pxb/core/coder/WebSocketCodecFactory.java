package com.pxb.core.coder;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**  
 * @ClassName: WebSocketCodecFactory  
 * @Description: webSocket编码解码工厂
 * @author panxiaobo  
 * @date 2017年7月27日 上午8:45:41  
 *    
 */
public class WebSocketCodecFactory implements ProtocolCodecFactory{
    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;

    public WebSocketCodecFactory() {
            encoder = new WebSocketEncoder();
            decoder = new WebSocketDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }    
}
