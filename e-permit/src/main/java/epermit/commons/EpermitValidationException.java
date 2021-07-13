package epermit.commons;

public class EpermitValidationException extends RuntimeException{
    private final ErrorCodes errorCode;

	public EpermitValidationException(String message, Throwable cause, ErrorCodes errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public EpermitValidationException(String message, ErrorCodes errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return this.errorCode.name();
	}
}
