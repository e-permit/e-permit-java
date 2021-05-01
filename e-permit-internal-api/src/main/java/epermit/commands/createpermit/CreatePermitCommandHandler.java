package epermit.commands.createpermit;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.transaction.Transactional;
import com.google.gson.Gson;

import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.common.JsonUtil;
import epermit.common.PermitUtil;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.entities.IssuedPermit;
import epermit.events.AppEventPublisher;
import epermit.events.permitcreated.PermitCreatedEventFactory;
import epermit.repositories.IssuedPermitRepository;
import epermit.services.KeyService;
import epermit.services.PermitService;
import lombok.SneakyThrows;

public class CreatePermitCommandHandler implements Command.Handler<CreatePermitCommand, CommandResult> {
        private final IssuedPermitRepository repository;
        private final PermitProperties properties;
        private final KeyService keyService;
        private final PermitCreatedEventFactory factory;
        private final AppEventPublisher eventPublisher;
        private final PermitService permitService;

        public CreatePermitCommandHandler(AppEventPublisher eventPublisher, PermitService permitService,
                        IssuedPermitRepository repository, PermitProperties properties, KeyService keyService,
                        PermitCreatedEventFactory factory) {
                this.repository = repository;
                this.properties = properties;
                this.keyService = keyService;
                this.factory = factory;
                this.eventPublisher = eventPublisher;
                this.permitService = permitService;
        }

        @Override
        @Transactional
        @SneakyThrows
        public CommandResult handle(CreatePermitCommand cmd) {
                Integer pid = permitService.generatePermitId(cmd.getIssuedFor(), cmd.getPermitYear(),
                                cmd.getPermitType());
                if (pid == null) {
                        return CommandResult.fail("NOT_SUFFICIENT_PERMITID", "Permit id is not sufficient");
                }
                IssuedPermit permit = Utils.convertCommandToPermit(cmd, properties.getIssuerCode(), pid);
                permit.setQrCode(permitService.generateQrCode(permit));
                repository.save(permit);
                CreatedEvent event = factory.create(permit);
                eventPublisher.publish(event);
                CommandResult result = CommandResult.success();
                return result;
        }

        public static class Utils {
                public static IssuedPermit convertCommandToPermit(CreatePermitCommand cmd, String issuer, Integer pid) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        Gson gson = JsonUtil.getGson();
                        String serialNumber = PermitUtil.getSerialNumber(issuer, cmd.getIssuedFor(),
                                        cmd.getPermitType(), cmd.getPermitYear(), pid);
                        String expireDate = "01/01/" + Integer.toString(cmd.getPermitYear() + 1);
                        IssuedPermit permit = new IssuedPermit();
                        permit.setIssuedFor(cmd.getIssuedFor());
                        permit.setClaims(gson.toJson(cmd.getClaims()));
                        permit.setCompanyName(cmd.getCompanyName());
                        permit.setExpireAt(OffsetDateTime.parse(expireDate, dtf).plusMonths(1).format(dtf));
                        permit.setIssuedAt(OffsetDateTime.now().format(dtf));
                        permit.setPermitId(pid);
                        permit.setPermitType(cmd.getPermitType());
                        permit.setPermitYear(cmd.getPermitYear());
                        permit.setPlateNumber(cmd.getPlateNumber());
                        permit.setSerialNumber(serialNumber);
                        return permit;
                }
        }
}
