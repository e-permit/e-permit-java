package epermit.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.messages.MessageJws;
import epermit.messages.MessageService;
import epermit.messages.createkey.CreateKeyMessage;
import epermit.messages.createpermit.CreatePermitMessage;
import epermit.messages.createquota.CreateQuotaMessage;
import epermit.messages.permitused.PermitUsedMessage;
import epermit.messages.revokepermit.RevokePermitMessage;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/create_permit")
    public String createPermit(@RequestBody MessageJws jws) {
        return messageService.handleMessage(jws.getJws(), CreatePermitMessage.class);
    }

    @PostMapping("/create_key")
    public String createKey(@RequestBody MessageJws jws) {
        return messageService.handleMessage(jws.getJws(), CreateKeyMessage.class);
    }

    @PostMapping("/create_quota")
    public String createQuota(@RequestBody MessageJws jws) {
        return messageService.handleMessage(jws.getJws(), CreateQuotaMessage.class);
    }

    @PostMapping("/revoke_permit")
    public String revokePermit(@RequestBody MessageJws jws) {
        return messageService.handleMessage(jws.getJws(), RevokePermitMessage.class);
    }

    @PostMapping("/permit_used")
    public String permitUsed(@RequestBody MessageJws jws) {
        return messageService.handleMessage(jws.getJws(), PermitUsedMessage.class);
    }
}
