package kr.hhplus.be.server.domain.outbox;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.support.EventType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "outbox")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outboxId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String payload;

    private LocalDateTime createdAt;


    public static OutboxEntity create(EventType eventType, String payload) {
        OutboxEntity outbox = new OutboxEntity();
        outbox.eventType = eventType;
        outbox.payload = payload;
        outbox.createdAt = LocalDateTime.now();
        return outbox;
    }
}
