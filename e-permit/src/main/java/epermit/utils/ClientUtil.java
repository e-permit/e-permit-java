package epermit.utils;

public class ClientUtil {
    public static String getXRoadUrl(String securityServer, String clientId) {
        return securityServer + "/r1/" + clientId + "/Permit";
    }
}
