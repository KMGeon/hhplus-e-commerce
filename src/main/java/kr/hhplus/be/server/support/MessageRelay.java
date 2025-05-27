package kr.hhplus.be.server.support;

import kr.hhplus.be.server.domain.outbox.OutboxEntity;
import kr.hhplus.be.server.domain.outbox.OutboxEvent;
import kr.hhplus.be.server.interfaces.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRelay {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> messageRelayKafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent outboxEvent) {
        outboxRepository.save(outboxEvent.getOutbox());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(OutboxEvent outboxEvent) {
        publishEvent(outboxEvent.getOutbox());
    }

    private void publishEvent(OutboxEntity outbox) {
        try {
            log.info("[MessageRelay] Kafka 발행: topic={}, payload={}", outbox.getEventType().getTopic(), outbox.getPayload());

            messageRelayKafkaTemplate.send(
                    outbox.getEventType().getTopic(),
                    outbox.getPayload()
            ).get(1, TimeUnit.SECONDS);

            outboxRepository.delete(outbox);
            log.info("[MessageRelay] 이벤트 발행 성공 및 아웃박스 정리 완료");

        } catch (Exception e) {
            /*
             * 실패 시 아웃박스에 남겨두어 재처리의 대상이 됨
             */
            log.error("[MessageRelay.publishEvent] 외부 시스템 발행 실패: outbox={}", outbox, e);

        }
    }

    @Scheduled(
            fixedDelay = 10,
            initialDelay = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "messageRelayPublishPendingEventExecutor"
    )
    public void publishPendingEvent() {
        List<OutboxEntity> outboxes = outboxRepository.findAllByCreatedAtLessThanEqualOrderByCreatedAtAsc(
                LocalDateTime.now().minusSeconds(10),
                Pageable.ofSize(100)
        );

        if (!outboxes.isEmpty())
            log.info("[MessageRelay] 미처리 이벤트 재처리 시작: {} 건", outboxes.size());


        for (OutboxEntity outbox : outboxes)
            publishEvent(outbox);
    }
}
