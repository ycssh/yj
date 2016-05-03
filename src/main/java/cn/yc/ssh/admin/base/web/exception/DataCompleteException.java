package cn.yc.ssh.admin.base.web.exception;

import org.apache.shiro.authc.AccountException;

/**
 * 恶意登录
 * @author ycssh
 *
 */
public class DataCompleteException extends AccountException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5581461930102745684L;

	public DataCompleteException(String message){
		super(message);
	}
	public DataCompleteException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataCompleteException() {
		super();
	}

	public DataCompleteException(Throwable cause) {
		super(cause);
	}
}
