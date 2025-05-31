package kr.hhplus.be.server.domain.support;

import kr.hhplus.be.server.domain.outbox.OutboxEntity;
import kr.hhplus.be.server.domain.outbox.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    public void publish(EventType type, EventPayload payload) {
        applicationEventPublisher.publishEvent(OutboxEvent.of(OutboxEntity.create(
                type,
                Event.of(
                        type,
                        payload
                ).toJson()
        )));
    }


}
