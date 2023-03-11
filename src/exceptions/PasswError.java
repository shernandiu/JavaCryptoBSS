package exceptions;

import java.security.GeneralSecurityException;

public class PasswError extends GeneralSecurityException {
	public PasswError() {
		super();
	}

	public PasswError(String msg) {
		super(msg);
	}
}
