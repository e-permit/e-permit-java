package epermit.commands.createpermit;

import java.time.OffsetDateTime;
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
import epermit.events.permitcreated.PermitCreatedEvent;
import epermit.events.permitcreated.PermitCreatedEventFactory;
import epermit.repositories.IssuedPermitRepository;
import epermit.services.EventService;
import epermit.services.PermitService;
import lombok.SneakyThrows;

public class CreatePermitCommandHandler implements Command.Handler<CreatePermitCommand, CommandResult> {
        private final IssuedPermitRepository repository;
        private final PermitProperties properties;
        private final PermitCreatedEventFactory factory;
        private final AppEventPublisher eventPublisher;
        private final PermitService permitService;
        private final EventService eventService;

        public CreatePermitCommandHandler(AppEventPublisher eventPublisher, PermitService permitService,
                        IssuedPermitRepository repository, PermitProperties properties,
                        PermitCreatedEventFactory factory, EventService eventService) {
                this.repository = repository;
                this.properties = properties;
                this.factory = factory;
                this.eventPublisher = eventPublisher;
                this.permitService = permitService;
                this.eventService = eventService;
        }

        @Override
        @Transactional
        @SneakyThrows
        public CommandResult handle(CreatePermitCommand cmd) {
                Integer serialNumber = permitService.generateSerialNumber(cmd.getIssuedFor(), cmd.getPermitYear(),
                                cmd.getPermitType());
                if (serialNumber == null) {
                        return CommandResult.fail("NOT_SUFFICIENT_PERMITID", "Permit id is not sufficient");
                }
                IssuedPermit permit = Utils.convertCommandToPermit(cmd, properties.getIssuerCode(), serialNumber);
                permit.setQrCode(permitService.generateQrCode(permit));
                repository.save(permit);
                PermitCreatedEvent event = factory.create(permit);
                eventService.setCommon(event, permit.getIssuedFor());
                CreatedEvent createdEvent = eventService.persist(event);
                eventPublisher.publish(createdEvent);
                CommandResult result = CommandResult.success();
                return result;
        }

        public static class Utils {
                public static IssuedPermit convertCommandToPermit(CreatePermitCommand cmd, String issuer, Integer serialNumber) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        Gson gson = JsonUtil.getGson();
                        String permitId = PermitUtil.getPermitId(issuer, cmd.getIssuedFor(),
                                        cmd.getPermitType(), cmd.getPermitYear(), serialNumber);
                        String expireDate = "01/01/" + Integer.toString(cmd.getPermitYear() + 1);
                        IssuedPermit permit = new IssuedPermit();
                        permit.setIssuedFor(cmd.getIssuedFor());
                        permit.setClaims(gson.toJson(cmd.getClaims()));
                        permit.setCompanyName(cmd.getCompanyName());
                        permit.setExpireAt(OffsetDateTime.parse(expireDate, dtf).plusMonths(1).format(dtf));
                        permit.setIssuedAt(OffsetDateTime.now().format(dtf));
                        permit.setSerialNumber(serialNumber);
                        permit.setPermitType(cmd.getPermitType());
                        permit.setPermitYear(cmd.getPermitYear());
                        permit.setPlateNumber(cmd.getPlateNumber());
                        permit.setPermitId(permitId);
                        return permit;
                }
        }
}
