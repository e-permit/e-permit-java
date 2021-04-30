package epermit.common;

import java.util.StringJoiner;

public class PermitUtil {
    public static String getSerialNumber(String iss, String aud, PermitType pt, Integer py,
            int pid) {
        StringJoiner joiner = new StringJoiner("-");
        String serialNumber = joiner.add(iss).add(aud).add(Integer.toString(py))
                .add(Integer.toString(pt.getCode())).add(Long.toString(pid)).toString();
        return serialNumber;
    }
}
