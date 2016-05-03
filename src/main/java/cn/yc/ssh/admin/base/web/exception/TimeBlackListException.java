package cn.yc.ssh.admin.base.web.exception;

import org.apache.shiro.authc.AccountException;

public class TimeBlackListException extends AccountException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5581461930102745684L;

	public TimeBlackListException(String message){
		super(message);
	}
	public TimeBlackListException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeBlackListException() {
		super();
	}

	public TimeBlackListException(Throwable cause) {
		super(cause);
	}
}
