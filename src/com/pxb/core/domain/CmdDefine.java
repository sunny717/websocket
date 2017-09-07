/**    
* @Title: CmdDefine.java  
* @Package com.pxb.core.domain  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年7月28日 上午9:40:17  
* @version V1.0.0    
*/  
package com.pxb.core.domain;

import java.lang.reflect.Method;

/**  
 * @ClassName: CmdDefine  
 * @Description: 命令对象定义  
 * @author panxiaobo  
 * @date 2017年7月28日 上午9:40:17  
 *    
 */
public class CmdDefine {
	
	private int cmd; //命令号
	private Object bean;  //实体对象
	private String methodName; //方法名
	private transient Method method;  //方法
	public int getCmd() {
		return cmd;
	}
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	public Object getBean() {
		return bean;
	}
	public void setBean(Object bean) {
		this.bean = bean;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	
	

}
