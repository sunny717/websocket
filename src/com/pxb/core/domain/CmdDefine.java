/**    
* @Title: CmdDefine.java  
* @Package com.pxb.core.domain  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��7��28�� ����9:40:17  
* @version V1.0.0    
*/  
package com.pxb.core.domain;

import java.lang.reflect.Method;

/**  
 * @ClassName: CmdDefine  
 * @Description: ���������  
 * @author panxiaobo  
 * @date 2017��7��28�� ����9:40:17  
 *    
 */
public class CmdDefine {
	
	private int cmd; //�����
	private Object bean;  //ʵ�����
	private String methodName; //������
	private transient Method method;  //����
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
