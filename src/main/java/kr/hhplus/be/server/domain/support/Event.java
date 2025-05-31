package kr.hhplus.be.server.domain.support;

import kr.hhplus.be.server.support.DataSerializer;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class Event<T extends EventPayload> {
    private EventType type;
    private T payload;

    public static Event<EventPayload> of(EventType type, EventPayload payload) {
        Event<EventPayload> event = new Event<>();
        event.type = type;
        event.payload = payload;
        return event;
    }

    public String toJson() {
        return DataSerializer.serialize(this);
    }

    public static <T extends EventPayload> Event<T> fromJson(String json, Class<T> payloadClass) {
        EventRaw eventRaw = DataSerializer.deserialize(json, EventRaw.class);
        if (eventRaw == null)
            return null;

        Event<T> event = new Event<>();
        event.type = EventType.from(eventRaw.type());
        event.payload = DataSerializer.deserialize(eventRaw.payload(), payloadClass);
        return event;
    }

    private record EventRaw(
            Long eventId,
            String type,
            Object payload
    ) {
    }
}
