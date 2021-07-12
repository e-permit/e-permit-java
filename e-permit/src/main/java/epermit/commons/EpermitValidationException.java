package epermit.commons;

public class EpermitValidationException extends RuntimeException{
    private final String errorCode;

    private final String errorId = "";


	public EpermitValidationException(String message, Throwable cause, String errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public EpermitValidationException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return this.errorCode;
	}
}
