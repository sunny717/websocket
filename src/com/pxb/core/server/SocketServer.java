/**    
* @Title: SocketServer.java  
* @Package com.pxb.server  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��7��27�� ����9:08:06  
* @version V1.0.0    
*/  
package com.pxb.core.server;

import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.IoEventQueueHandler;
import org.apache.mina.filter.executor.UnorderedThreadPoolExecutor;
import org.apache.mina.filter.firewall.ConnectionThrottleFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.util.ExceptionMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pxb.core.net.MyOrderedThreadPoolExecutor;
import com.pxb.core.net.NamingThreadFactory;

/**  
 * @ClassName: SocketServer  
 * @Description: socket������  
 * @author panxiaobo  
 * @date 2017��7��27�� ����9:08:06  
 *    
 */
public class SocketServer {
	private static Logger logger = LoggerFactory.getLogger(SocketServer.class);

	private NioSocketAcceptor acceptor = null;
	private int port = 8739;
	private String ip = "127.0.0.1";
	private String serverName;

	/**
	 * APP��������
	 */
	private Properties appProp;
	/**
	 * ���롢����������
	 */
	private ProtocolCodecFactory codecFactory;

	/**
	 * ��Ϣ������
	 */
	private IoProcessor<NioSession> processor;

	/**
	 * Mina ��IOProcess����Ϣ�������̳߳ص��߳���
	 */
	private int processorNum = Runtime.getRuntime().availableProcessors() * 2 + 1;// Runtime.getRuntime().availableProcessors()*2
	// +
	// 1;

	// ����accept �̳߳�,.����ز���Ҫ
	// private ExecutorService exector;

	/**
	 *��Ϣִ����
	 */
	private IoHandler handler;

	/**
	 * IoHandler ��Ϣִ�������̳߳أ������WRITE�������IO�¼���������رգ��ȵ�
	 */
	private ThreadPoolExecutor executor;
	/**
	 * IoHandler ��Ϣִ����дsocket�̳߳�
	 */
	private ThreadPoolExecutor executorWrite;
	/**
	 * ��Ϣִ�����̳߳ص���С�߳���
	 */
	private int corePoolSize = Runtime.getRuntime().availableProcessors() * 4;

	/**
	 * ��Ϣִ�����̳߳ص�����߳�������д�ֿ���Ϊ�˷�ֹ�����Ķ�����������д
	 */
	private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 24 + 1;

	/**
	 * ��Ϣִ�����̳߳س���corePoolSize��Thread���ʱ��;��
	 */
	private long keepAliveTime = 300;

	/**
	 * useOrderedPool=false ,ʹ��UnorderedThreadPoolExecutor useOrderedPool=true
	 * ,ʹ��OrderedThreadPoolExecutor��ͬһ��session�����������˳��ִ�У�����ÿ��sessionһ������
	 */
	private boolean useOrderedPool = false;

	/**
	 * useOrderedPool=true��ʱ����Ч,ָ��һ��session��������е���󳤶ȣ�����������ȵ�������.
	 */
	private int orderedPoolMaxQueueSize = 20;
	/**
	 * ��Ϣִ�����̳߳�������е�handler,Ŀǰֻ���ڶ�����������handler,д����û��
	 * handler��������������г��ȵ����ƣ�Ҳ�����������log
	 */
	private IoEventQueueHandler ioEventQueueHandler;
	/**
	 * ���ͻ���������ʱ���� ��Ĭ��60��,1����
	 */
	private int keepAliveRequestInterval = 120;//

	/**
	 * ������ʱ�Ĵ�����
	 */
	private KeepAliveRequestTimeoutHandler keepAliveHandler;

	// private ResultMgr resultMgr = new ResultMgr();

	private int serverType;// server������

	/**
	 * =1�ڱ���֮ǰ������֮��ʹ��logfilter,=2�ڱ���֮�󡢽���֮ǰʹ��logfilter��else����ʹ��logfilter
	 */
	private int useLogFilter = 1;

	/**
	 * �Ƿ�ʹ��tcp������ SocketOptions.TCP_NODELAY; Ĭ����false;
	 */
	private boolean tcpNoDelay = true;

	/**
	 * �Ƿ�ʹ�÷����д�̳߳� �����򵥲��ԣ��ƺ�write������һ���̳߳ز���������ܣ�������������
	 */
	private boolean useWriteThreadPool = false;

	private int backlog = 400;// ���ȴ�socket���ӵ�����,Ĭ��Ϊ50

	private boolean bind0 = true;// �Ƿ�bind��0.0.0.0

	/**
	 * ͬһ��ip,���ٺ���֮�������ٴν���һ��������<br/>
	 * ���Ҫ�������ã���Ϊ�����ҿ�������ͬ��Ip
	 */
	@Deprecated
	private long connectionAllowedInterval = 0;

	public void setConnectionAllowedInterval(long connectionAllowedInterval) {
		this.connectionAllowedInterval = connectionAllowedInterval;
	}

	public void setKeepAliveHandler(
			KeepAliveRequestTimeoutHandler keepAliveHandler) {
		this.keepAliveHandler = keepAliveHandler;
	}

	public void setBind0(boolean bind0) {
		this.bind0 = bind0;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public void setUseWriteThreadPool(boolean useWriteThreadPool) {
		this.useWriteThreadPool = useWriteThreadPool;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public void setUseLogFilter(int useLogFilter) {
		this.useLogFilter = useLogFilter;
	}
	public void setAppProp(Properties appProp) {
		this.appProp = appProp;
	}

	public void setKeepAliveRequestInterval(int keepAliveRequestInterval) {
		this.keepAliveRequestInterval = keepAliveRequestInterval;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public void setProcessorNum(int processorNum) {
		this.processorNum = processorNum;
	}

	public void setUseOrderedPool(boolean useOrderedPool) {
		this.useOrderedPool = useOrderedPool;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAddress() {
		return ip + ":" + port;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}

	public void setCodecFactory(ProtocolCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}

	public int getServerType() {
		return serverType;
	}

	public void setServerType(int serverType) {
		this.serverType = serverType;
	}

	public int getPort() {
		return port;
	}

	public String getIp() {
		return ip;
	}

	public void setIoEventQueueHandler(IoEventQueueHandler ioEventQueueHandler) {
		this.ioEventQueueHandler = ioEventQueueHandler;
	}

	public void setOrderedPoolMaxQueueSize(int orderedPoolMaxQueueSize) {
		this.orderedPoolMaxQueueSize = orderedPoolMaxQueueSize;
	}


	public void start() {

		logger.info("��ʼ����socket server:{}...", serverName);

		logger.info("wolfServer��Ϣ����������:{}", processorNum);

		logger.info("wolfServer��Ϣִ�����߳���:{}/{}", corePoolSize, maxPoolSize);

		// String threadPrefix = serverName + "[" + this.port + "]";
		// ִ��������accept,�б�Ҫ��pool? ʵ����ֻ��һ���߳��ڼ���
		// exector = Executors.newFixedThreadPool(threadNum,
		// new NamingThreadFactory(threadPrefix));

		// SimpleIoProcessorPool�õ���cachePool
		//
		processor = new SimpleIoProcessorPool<NioSession>(NioProcessor.class,
				processorNum);
		// acceptor = new NioSocketAcceptor((Executor) exector, processor);
		acceptor = new NioSocketAcceptor(processor);

		// ���Ӷ��е���󳤶ȣ�Ĭ��50
		// acceptor.setBacklog(100);
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

		// if (logger.isDebugEnabled()) {
		// profiler = new ProfilerTimerFilter(TimeUnit.MILLISECONDS,
		// IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT);
		// chain.addFirst("profiler", profiler);
		// }

		if (connectionAllowedInterval > 0) {
			chain.addLast("throttle", new ConnectionThrottleFilter(
					connectionAllowedInterval));
		}
		if (useLogFilter == 2) {
			chain.addLast("logging", new LoggingFilter());
		}

		// ʹ���ַ�������
		// ����ǰ̨���̨�Ľ�������̨server֮��Ľ�����ʹ�õ���AMF3CodecFactory,��Ҫ�ǿ��ǵ�AMF3��encode/decode��java�Ŀ�
		// ���Ҫ�ֿ�ʹ�ò�ͬ��encode/decode,��ʹ��DemuxingProtocolEncoder��DemuxingProtocolDecoder�����༰DemuxingProtocolCodecFactory
		// codec filterҪ����ExecutorFilterǰ����Ϊ��дͬһ��socket connection��socket
		// buf���ܲ�������ʵ����Ҫ�Ƕ���д����mina�Ѿ���װ��һ��write Queue)
		chain.addLast("codec", new ProtocolCodecFilter(this.codecFactory));

		// ���ִ���̳߳�

		// ��������UnorderedThreadPoolExecutor�̳߳أ�Ҳ����ʹ��OrderedThreadPoolExecutor
		// ʹ��OrderedThreadPoolExecutorʱ��ͬһ��ioSession�е������ǰ�˳����ģ�������ڳ�ʱ��ķ��������ܻ����������
		// ������gameserver��ΪmainServer��client,ע�ⳤʱ��ķ��������ܻ�����������socket����,����gameServer��mainServer���ִ��������
		// ���ﲻҪʹ��jdkĬ�ϵ�ThreadPoolExecutor+LinkedBlockingQueue,JDK��ThreadPoolExecutor+LinkedBlockingQueue��maxPoolSize������������
		if (useOrderedPool) {
			logger.info("ʹ��OrderedThreadPoolExecutor");
			// executor = new OrderedThreadPoolExecutor(corePoolSize,
			// maxPoolSize,
			// keepAliveTime, TimeUnit.SECONDS, new NamingThreadFactory(
			// "WolfServerThread"), ioEventQueueHandler);

			executor = new MyOrderedThreadPoolExecutor(corePoolSize,
					maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
					new NamingThreadFactory("WolfServerThread"),
					ioEventQueueHandler, orderedPoolMaxQueueSize);
			//
		} else {
			logger.info("ʹ��UnorderedThreadPoolExecutor");
			executor = new UnorderedThreadPoolExecutor(corePoolSize,
					maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
					new NamingThreadFactory("WolfServerThread"),
					ioEventQueueHandler);
		}
		// ������Ԥ������corePoolSize�������߳�
		executor.prestartAllCoreThreads();
		chain.addLast("exec", new ExecutorFilter(executor,
				IoEventType.EXCEPTION_CAUGHT, IoEventType.MESSAGE_RECEIVED,
				IoEventType.SESSION_CLOSED, IoEventType.SESSION_IDLE,
				IoEventType.SESSION_OPENED));

		if (useWriteThreadPool) {
			// �ƺ�write������һ���̳߳ز���������ܣ�������������,Ĭ��д��ʹ�ö������̳߳أ�ֱ����mina io�߳��д���
			executorWrite = new UnorderedThreadPoolExecutor(corePoolSize,
					maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
					new NamingThreadFactory("WolfServerWriteThread"));
			executorWrite.prestartAllCoreThreads();
			// readerҪ����ҵ���߼���executorWriteִ�б�reader�죬����Ԥ������
			// executorWrite.prestartAllCoreThreads();
			chain.addLast("execWrite", new ExecutorFilter(executorWrite,
					IoEventType.WRITE, IoEventType.MESSAGE_SENT));
		}

		// ����handler�� logger,��codec֮�󣬴�ӡ����decodeǰ����encode�����Ϣ��log
		// ����������ExecutorFilter֮����Ϊ���ڹ����߳��д�ӡlog,������NioProcessor�д�ӡ
		if (useLogFilter == 1) {
			chain.addLast("logging", new LoggingFilter());
		}

		// ����Server
		try {

			acceptor.setHandler(handler);

			// socket��close֮��������������ʹ�ã����Ǵ���TIME_WAITTING״̬�������ʾ��������TIME_WAITTING״̬�Ķ˿�
			// ������������
			acceptor.setReuseAddress(true);
			// ����ÿһ�����ӵĶ˿ڿ�������
			SocketSessionConfig config = acceptor.getSessionConfig();
			config.setReuseAddress(true);
			// config.set

			// ����Ϊ���ӳٷ��ͣ�Ϊtrue����װ�ɴ�����ͣ��յ��������Ϸ���,
			// acceptor.getSessionConfig().setTcpNoDelay(true);
			config.setTcpNoDelay(tcpNoDelay);

			// config.setReaderIdleTime(idleTime)

			// ��ò�Ҫ���û�������С��ʹ�ò���ϵͳĬ�ϵĻ�������С
			// acceptor.getSessionConfig().setReceiveBufferSize(1024);//�������뻺��
			// ���Ĵ�С
			// acceptor.getSessionConfig().setSendBufferSize(10240);//���������
			// �����Ĵ�С

			// ��������������˿ڵļ������е����ֵΪ100�������ǰ�Ѿ���100�����ӣ����µ� �����������������ܾ�(Ĭ��ֵΪ50)
			acceptor.setBacklog(this.backlog);
			if (bind0) {
				acceptor.bind(new InetSocketAddress("0.0.0.0", port));
				logger.info("socket bind on 0.0.0.0:{}", port);
			} else {
				acceptor.bind(new InetSocketAddress(ip, port));
				logger.info("socket bind on {}:{}", ip, port);
			}
		} catch (Exception e) {
			ExceptionMonitor.getInstance().exceptionCaught(e);
			throw new RuntimeException(e);
		}

		// Ϊ�˷�������ͬ���ص��ͻ��˷���
		// NodeSessionMgr.setResultMgr(resultMgr);

		// Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		// public void run() {
		// stop();
		// }
		// }));
		logger.info("socket server�����ɹ�:{}:{}...", ip, port);

		// if (logger.isDebugEnabled()) {
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// while (acceptor.isActive()) {
		// logger.debug("ִ�ж��г���I/O:"
		// + executor.getQueue().size()
		// + "/"
		// + (executorWrite == null ? 0 : executorWrite
		// .getQueue().size()));
		//
		// logger.debug("�ڴ�,����/����:"
		// + Runtime.getRuntime().totalMemory() + "/"
		// + Runtime.getRuntime().freeMemory());
		//
		// // System.gc();
		// try {
		// Thread.sleep(30000);
		// } catch (Exception e) {
		// }
		// }
		// }
		// }).start();
		// }
	}

	public void stop() {
		// logger.info("wolf Server ExectorService released!!");
		if (this.acceptor != null) {
			logger.error("����ֹͣ����:{}:{}...", ip, port);
			acceptor.unbind();
			acceptor.dispose();
			acceptor = null;
			if (this.processor != null) {
				this.processor.dispose();
			}
			if (this.executor != null) {
				this.executor.shutdown();
			}
			if (executorWrite != null) {
				executorWrite.shutdown();
			}
			logger.info("socket serverֹͣ:{}:{}...", ip, port);
		}

	}

	public IoHandler getHandler() {
		return handler;
	}

	// public ResultMgr getResultMgr() {
	// return resultMgr;
	// }
}
