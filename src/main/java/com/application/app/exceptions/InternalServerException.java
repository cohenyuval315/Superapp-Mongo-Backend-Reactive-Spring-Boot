package com.application.app.exceptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException { 

	private static final long serialVersionUID = -4379903955494285547L;
	private Log logger = LogFactory.getLog(InternalServerException.class);
	
	
	public InternalServerException() {
		super("internal server error");
	}

	public InternalServerException(String message) {
		super(message);
		logger.error(message);
	}

	public InternalServerException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InternalServerException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InternalServerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
