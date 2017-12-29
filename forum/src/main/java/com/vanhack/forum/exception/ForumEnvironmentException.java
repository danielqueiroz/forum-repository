package com.vanhack.forum.exception;

public class ForumEnvironmentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7988915690053556656L;

	public ForumEnvironmentException(String message) {
		super(message);
	}
	
	public ForumEnvironmentException(String message, Throwable e) {
		super(message, e);
	}
	
	
	public ForumEnvironmentException(Throwable e) {
		super(e);
	}
}
