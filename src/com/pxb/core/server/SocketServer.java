/**    
* @Title: SocketServer.java  
* @Package com.pxb.server  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月27日 上午9:08:06  
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
 * @Description: socket服务器  
 * @author panxiaobo  
 * @date 2017年7月27日 上午9:08:06  
 *    
 */
public class SocketServer {
	private static Logger logger = LoggerFactory.getLogger(SocketServer.class);

	private NioSocketAcceptor acceptor = null;
	private int port = 8739;
	private String ip = "127.0.0.1";
	private String serverName;

	/**
	 * APP环境变量
	 */
	private Properties appProp;
	/**
	 * 编码、解码器工厂
	 */
	private ProtocolCodecFactory codecFactory;

	/**
	 * 消息处理器
	 */
	private IoProcessor<NioSession> processor;

	/**
	 * Mina 的IOProcess的消息处理器线程池的线程数
	 */
	private int processorNum = Runtime.getRuntime().availableProcessors() * 2 + 1;// Runtime.getRuntime().availableProcessors()*2
	// +
	// 1;

	// 监听accept 线程池,.这个池不重要
	// private ExecutorService exector;

	/**
	 *消息执行器
	 */
	private IoHandler handler;

	/**
	 * IoHandler 消息执行器读线程池，处理除WRITE外的其他IO事件，如读，关闭，等等
	 */
	private ThreadPoolExecutor executor;
	/**
	 * IoHandler 消息执行器写socket线程池
	 */
	private ThreadPoolExecutor executorWrite;
	/**
	 * 消息执行器线程池的最小线程数
	 */
	private int corePoolSize = Runtime.getRuntime().availableProcessors() * 4;

	/**
	 * 消息执行器线程池的最大线程数，读写分开是为了防止大量的读请求阻塞回写
	 */
	private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 24 + 1;

	/**
	 * 消息执行器线程池超过corePoolSize的Thread存活时间;秒
	 */
	private long keepAliveTime = 300;

	/**
	 * useOrderedPool=false ,使用UnorderedThreadPoolExecutor useOrderedPool=true
	 * ,使用OrderedThreadPoolExecutor，同一个session过来的请求会顺序执行，并且每个session一个队列
	 */
	private boolean useOrderedPool = false;

	/**
	 * useOrderedPool=true的时候有效,指定一个session的请求队列的最大长度，超过这个长度的请求丢弃.
	 */
	private int orderedPoolMaxQueueSize = 20;
	/**
	 * 消息执行器线程池请求队列的handler,目前只是在读请求配置了handler,写请求没有
	 * handler可以用来处理队列长度的限制，也可以用来输出log
	 */
	private IoEventQueueHandler ioEventQueueHandler;
	/**
	 * 检查客户端心跳的时间间隔 ，默认60秒,1分钟
	 */
	private int keepAliveRequestInterval = 120;//

	/**
	 * 心跳超时的处理器
	 */
	private KeepAliveRequestTimeoutHandler keepAliveHandler;

	// private ResultMgr resultMgr = new ResultMgr();

	private int serverType;// server的类型

	/**
	 * =1在编码之前、解码之后使用logfilter,=2在编码之后、解码之前使用logfilter，else，不使用logfilter
	 */
	private int useLogFilter = 1;

	/**
	 * 是否使用tcp参数： SocketOptions.TCP_NODELAY; 默认是false;
	 */
	private boolean tcpNoDelay = true;

	/**
	 * 是否使用分离的写线程池 经过简单测试，似乎write独立出一个线程池不能提高性能，反倒降低性能
	 */
	private boolean useWriteThreadPool = false;

	private int backlog = 400;// 最大等待socket连接的数量,默认为50

	private boolean bind0 = true;// 是否bind到0.0.0.0

	/**
	 * 同一个ip,多少毫秒之内允许再次建立一个新连接<br/>
	 * 这个要慎重配置，因为多个玩家可能是相同的Ip
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

		logger.info("开始启动socket server:{}...", serverName);

		logger.info("wolfServer消息处理器数量:{}", processorNum);

		logger.info("wolfServer消息执行器线程数:{}/{}", corePoolSize, maxPoolSize);

		// String threadPrefix = serverName + "[" + this.port + "]";
		// 执行器用于accept,有必要用pool? 实际上只有一个线程在监听
		// exector = Executors.newFixedThreadPool(threadNum,
		// new NamingThreadFactory(threadPrefix));

		// SimpleIoProcessorPool用的是cachePool
		//
		processor = new SimpleIoProcessorPool<NioSession>(NioProcessor.class,
				processorNum);
		// acceptor = new NioSocketAcceptor((Executor) exector, processor);
		acceptor = new NioSocketAcceptor(processor);

		// 连接队列的最大长度，默认50
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

		// 使用字符串编码
		// 这里前台与后台的交互。后台server之间的交互都使用的是AMF3CodecFactory,主要是考虑到AMF3的encode/decode比java的快
		// 如果要分开使用不同的encode/decode,请使用DemuxingProtocolEncoder和DemuxingProtocolDecoder两个类及DemuxingProtocolCodecFactory
		// codec filter要放在ExecutorFilter前，因为读写同一个socket connection的socket
		// buf不能并发（事实上主要是读，写操作mina已经封装成一个write Queue)
		chain.addLast("codec", new ProtocolCodecFilter(this.codecFactory));

		// 添加执行线程池

		// 这里用了UnorderedThreadPoolExecutor线程池，也可以使用OrderedThreadPoolExecutor
		// 使用OrderedThreadPoolExecutor时，同一个ioSession中的请求是按顺序处理的，如果存在长时间的方法，可能会造成阻塞，
		// 尤其是gameserver作为mainServer的client,注意长时间的方法，可能会阻塞后续的socket请求,导致gameServer与mainServer出现大面积阻塞
		// 这里不要使用jdk默认的ThreadPoolExecutor+LinkedBlockingQueue,JDK的ThreadPoolExecutor+LinkedBlockingQueue中maxPoolSize基本不起作用
		if (useOrderedPool) {
			logger.info("使用OrderedThreadPoolExecutor");
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
			logger.info("使用UnorderedThreadPoolExecutor");
			executor = new UnorderedThreadPoolExecutor(corePoolSize,
					maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
					new NamingThreadFactory("WolfServerThread"),
					ioEventQueueHandler);
		}
		// 这里是预先启动corePoolSize个处理线程
		executor.prestartAllCoreThreads();
		chain.addLast("exec", new ExecutorFilter(executor,
				IoEventType.EXCEPTION_CAUGHT, IoEventType.MESSAGE_RECEIVED,
				IoEventType.SESSION_CLOSED, IoEventType.SESSION_IDLE,
				IoEventType.SESSION_OPENED));

		if (useWriteThreadPool) {
			// 似乎write独立出一个线程池不能提高性能，反倒降低性能,默认写不使用独立的线程池，直接在mina io线程中处理
			executorWrite = new UnorderedThreadPoolExecutor(corePoolSize,
					maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
					new NamingThreadFactory("WolfServerWriteThread"));
			executorWrite.prestartAllCoreThreads();
			// reader要处理业务逻辑，executorWrite执行比reader快，不用预先启动
			// executorWrite.prestartAllCoreThreads();
			chain.addLast("execWrite", new ExecutorFilter(executorWrite,
					IoEventType.WRITE, IoEventType.MESSAGE_SENT));
		}

		// 配置handler的 logger,在codec之后，打印的是decode前或者encode后的消息的log
		// 可以配置在ExecutorFilter之后：是为了在工作线程中打印log,不是在NioProcessor中打印
		if (useLogFilter == 1) {
			chain.addLast("logging", new LoggingFilter());
		}

		// 启动Server
		try {

			acceptor.setHandler(handler);

			// socket在close之后不是立即可以在使用，而是处在TIME_WAITTING状态，这里表示可以重用TIME_WAITTING状态的端口
			// 主监听可重用
			acceptor.setReuseAddress(true);
			// 设置每一个连接的端口可以重用
			SocketSessionConfig config = acceptor.getSessionConfig();
			config.setReuseAddress(true);
			// config.set

			// 设置为非延迟发送，为true则不组装成大包发送，收到东西马上发出,
			// acceptor.getSessionConfig().setTcpNoDelay(true);
			config.setTcpNoDelay(tcpNoDelay);

			// config.setReaderIdleTime(idleTime)

			// 最好不要设置缓冲区大小，使用操作系统默认的缓冲区大小
			// acceptor.getSessionConfig().setReceiveBufferSize(1024);//设置输入缓冲
			// 区的大小
			// acceptor.getSessionConfig().setSendBufferSize(10240);//设置输出缓
			// 冲区的大小

			// 设置主服务监听端口的监听队列的最大值为100，如果当前已经有100个连接，再新的 连接来将被服务器拒绝(默认值为50)
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

		// 为了服务器端同步回调客户端方法
		// NodeSessionMgr.setResultMgr(resultMgr);

		// Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		// public void run() {
		// stop();
		// }
		// }));
		logger.info("socket server启动成功:{}:{}...", ip, port);

		// if (logger.isDebugEnabled()) {
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// while (acceptor.isActive()) {
		// logger.debug("执行队列长度I/O:"
		// + executor.getQueue().size()
		// + "/"
		// + (executorWrite == null ? 0 : executorWrite
		// .getQueue().size()));
		//
		// logger.debug("内存,分配/空闲:"
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
			logger.error("正在停止服务:{}:{}...", ip, port);
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
			logger.info("socket server停止:{}:{}...", ip, port);
		}

	}

	public IoHandler getHandler() {
		return handler;
	}

	// public ResultMgr getResultMgr() {
	// return resultMgr;
	// }
}
