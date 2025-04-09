package epermit.controllers;

import java.util.List;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nimbusds.jwt.SignedJWT;

import epermit.entities.LedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.HealthCheckRemoteResult;
import epermit.repositories.LedgerEventRepository;
import epermit.services.AuthorityService;
import epermit.services.ConfigService;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping
public class IndexController {
    private final BuildProperties buildProperties;
    private final EPermitProperties properties;
    private final ConfigService configService;
    private final AuthorityService authorityService;
    private final LedgerEventRepository ledgerEventRepository;
    private final JwsUtil jwsUtil;

    @GetMapping("/")
    public AuthorityConfig getConfig() {
        AuthorityConfig config = configService.getConfig();
        config.setVersion(buildProperties.getVersion());
        return config;
    }

    @GetMapping("/authorities")
    public List<String> getAuthorities() {
        return authorityService.getAll().stream().map(x -> x.getPublicApiUri()).toList();
    }

    @GetMapping("/healthcheck")
    @SneakyThrows
    public HealthCheckRemoteResult healthcheck(@RequestHeader HttpHeaders headers) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header not found");
        }
        if (!authorization.toLowerCase().startsWith("bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization type");
        }
        String jwt = authorization.substring(7);
        if (!jwsUtil.verifyJwt(jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization failed");
        }
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        String issuer = signedJWT.getJWTClaimsSet().getIssuer();
        LedgerEvent to = ledgerEventRepository
                .findTopByProducerAndConsumerOrderByCreatedAtDesc(issuer, properties.getIssuerCode())
                .orElse(new LedgerEvent());
        LedgerEvent from = ledgerEventRepository
                .findTopByProducerAndConsumerOrderByCreatedAtDesc(properties.getIssuerCode(), issuer)
                .orElse(new LedgerEvent());
        HealthCheckRemoteResult result = new HealthCheckRemoteResult();
        result.setToLastEventId(to.getEventId());
        result.setFromLastEventId(from.getEventId());
        return result;
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }
}