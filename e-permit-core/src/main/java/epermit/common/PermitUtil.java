package epermit.common;

import java.util.StringJoiner;

public class PermitUtil {
    public static String getPermitId(String iss, String aud, PermitType pt, Integer py,
            int serialNumber) {
        StringJoiner joiner = new StringJoiner("-");
        String permitId = joiner.add(iss).add(aud).add(Integer.toString(py))
                .add(Integer.toString(pt.getCode())).add(Long.toString(serialNumber)).toString();
        return permitId;
    }
}
