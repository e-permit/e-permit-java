package epermit.commons;

public class Check {
    public static void assertTrue(Boolean condition, ErrorCodes errorCode) {
        if (!condition) {
            throw new EpermitValidationException(errorCode.name(), errorCode);
        }
    }

    public static void assertFalse(Boolean condition, ErrorCodes errorCode) {
        if (condition) {
            throw new EpermitValidationException(errorCode.name(), errorCode);
        }
    }

    public static void assertEquals(Object first, Object last, ErrorCodes errorCode) {
        if (!first.equals(last)) {
            throw new EpermitValidationException(errorCode.name(), errorCode);
        }
    }
}
