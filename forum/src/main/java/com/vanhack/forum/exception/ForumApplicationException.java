package com.vanhack.forum.exception;

public class ForumApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7432899868977695018L;

	public ForumApplicationException(String message) {
		super(message);
	}
	
	public ForumApplicationException(String message, Throwable e) {
		super(message, e);
	}
	
	public ForumApplicationException(Throwable e) {
		super(e);
	}

}
