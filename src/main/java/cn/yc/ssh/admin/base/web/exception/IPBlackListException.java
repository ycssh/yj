package cn.yc.ssh.admin.base.web.exception;

import org.apache.shiro.authc.AccountException;

public class IPBlackListException extends AccountException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5581461930102745684L;

	public IPBlackListException(String message){
		super(message);
	}
	public IPBlackListException(String message, Throwable cause) {
		super(message, cause);
	}

	public IPBlackListException() {
		super();
	}

	public IPBlackListException(Throwable cause) {
		super(cause);
	}
}
