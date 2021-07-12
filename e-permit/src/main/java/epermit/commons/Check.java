package epermit.commons;

public class Check {
    public static void assertTrue(Boolean condition, String errorCode) {
        if (condition) {
            throw new EpermitValidationException(errorCode, "");
        }
    }

    public static void assertEquals(Object first, Object last, String errorCode) {
        if (!first.equals(last)) {
            throw new EpermitValidationException(errorCode, "");
        }
    }
}
