package com.pxb.core.net;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;



/**  
 * @ClassName: NamingThreadFactory  
 * @Description: 用来给所有的线程起名字，避免对于死锁的线程不知所�?
 * @author panxiaobo  
 * @date 2017�?�?7�?上午9:08:06  
 *    
 */
public class NamingThreadFactory implements ThreadFactory {
     private final ThreadGroup group;
     private final AtomicInteger threadNumber = new AtomicInteger(1);
     final String namePrefix;

     public NamingThreadFactory(String groupName) {
         SecurityManager s = System.getSecurityManager();
         group = (s != null)? s.getThreadGroup() :
                              Thread.currentThread().getThreadGroup();
         namePrefix = groupName == null ? "thread-" : groupName;
     }

     public Thread newThread(Runnable r) {
         Thread t = new Thread(group, r,
                               namePrefix + "-" + threadNumber.getAndIncrement(),
                               0);
         if (t.isDaemon())
             t.setDaemon(false);
         if (t.getPriority() != Thread.NORM_PRIORITY)
             t.setPriority(Thread.NORM_PRIORITY);
         
         return t;
     }

}