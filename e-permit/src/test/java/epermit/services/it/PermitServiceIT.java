package epermit.services.it;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.entities.LedgerQuota;
import epermit.entities.SerialNumber;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.models.enums.SerialNumberState;
import epermit.models.inputs.CreatePermitInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.repositories.SerialNumberRepository;
import epermit.services.PermitService;
import epermit.services.PrivateKeyService;
import epermit.utils.PermitUtil;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PermitServiceIT {

    @Autowired
    private SerialNumberRepository serialNumberRepository;

    @Autowired
    private EPermitProperties properties;

    @Autowired
    private PermitUtil permitUtil;

    @Autowired
    private LedgerEventUtil ledgerEventUtil;

    @Autowired
    private LedgerPermitRepository permitRepository;


    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();


    @BeforeAll
    @Transactional
    static void up(@Autowired PrivateKeyService privateKeyService,
            @Autowired PrivateKeyRepository keyRepository) {
        if (keyRepository.count() == 0) {
            privateKeyService.seed();
        }
    }

    @BeforeAll
    @Transactional
    static void setUp(@Autowired AuthorityRepository authorityRepository,
            @Autowired LedgerQuotaRepository ledgerQuotaRepository,
            @Autowired SerialNumberRepository serialNumberRepository) {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("FR");
        authority.setName("Uz");
        authorityRepository.save(authority);

        LedgerQuota quota = new LedgerQuota();
        quota.setActive(true);
        quota.setEndNumber(100);
        quota.setPermitIssuedFor("FR");
        quota.setPermitIssuer("TR");
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        quota.setStartNumber(1);
        ledgerQuotaRepository.save(quota);
        List<SerialNumber> serialNumbers = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            SerialNumber serialNumber = new SerialNumber();
            serialNumber.setSerialNumber(i);
            serialNumber.setAuthorityCode("FR");
            serialNumber.setPermitType(PermitType.BILITERAL);
            serialNumber.setPermitYear(2021);
            serialNumber.setState(SerialNumberState.CREATED);
            serialNumbers.add(serialNumber);
        }
        serialNumberRepository.saveAll(serialNumbers);
    }

    @Test
    void permitCreatedTest() {
        PermitService permitService = new PermitService(permitUtil, properties, ledgerEventUtil,
                new ModelMapper(), permitRepository, serialNumberRepository);
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyId("ABC");
        input.setCompanyName("ABC");
        input.setIssuedFor("FR");
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setPlateNumber("ABC");
        permitService.createPermit(input);
    }
}


