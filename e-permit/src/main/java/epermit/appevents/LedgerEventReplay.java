package epermit.appevents;

import lombok.Data;

@Data
public class LedgerEventReplay {
    private LedgerEventCreated eventCreated;
}
