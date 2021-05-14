package epermit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.CreatePermitInput;
import epermit.models.EPermitProperties;
import epermit.models.IssuedPermit;
import epermit.models.PermitType;
import epermit.services.PermitService;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class PermitUtilTest {
    @Mock
    JwsUtil jwsUtil;

    @Mock
    EPermitProperties properties;

    @Mock
    PermitService permitService;

    @InjectMocks
    PermitUtil util;

    @Test
    void getPermitTest() {
        String permitId = util.getPermitId("TR", "UA", PermitType.BILITERAL, 2021, 12);
        assertEquals("TR-UA-2021-1-12", permitId);
    }

    @Test
    void createPermitTest() {
        when(properties.getIssuerCode()).thenReturn("UA");
        when(permitService.getSerialNumber("TR", 2021, PermitType.BILITERAL)).thenReturn(1);
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyName("companyName");
        input.setIssuedFor("TR");
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setPlateNumber("plateNumber");
        Map<String, Object> claims = new HashMap<>();
        claims.put("key", "value");
        input.setClaims(claims);
        IssuedPermit permit = util.createPermit(input);
        assertNotNull(permit);
        assertEquals("companyName", permit.getCompanyName());
        assertEquals("UA-TR-2021-1-1", permit.getPermitId());
    }
}
