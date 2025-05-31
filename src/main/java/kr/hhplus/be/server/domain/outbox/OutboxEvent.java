package kr.hhplus.be.server.domain.outbox;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OutboxEvent {
    private OutboxEntity outbox;
    public static OutboxEvent of(OutboxEntity outbox) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.outbox = outbox;
        return outboxEvent;
    }
}
