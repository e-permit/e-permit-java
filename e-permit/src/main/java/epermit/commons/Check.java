package epermit.commons;

public class Check {
    public static void isTrue(Boolean condition, ErrorCodes errorCode) {
        if (condition) {
            throw new EpermitValidationException(errorCode.name(), errorCode);
        }
    }

    public static void equals(Object first, Object last, ErrorCodes errorCode) {
        if (!first.equals(last)) {
            throw new EpermitValidationException(errorCode.name(), errorCode);
        }
    }
}
