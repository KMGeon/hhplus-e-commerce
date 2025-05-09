package kr.hhplus.be.server.concurrency;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class ExecutionOrderRecorder {
    private final List<String> executionOrder = new ArrayList<>();

    public void recordEvent(String eventName) {
        executionOrder.add(eventName);
    }

    public List<String> getExecutionOrder() {
        return new ArrayList<>(executionOrder);
    }

    public void clear() {
        executionOrder.clear();
    }
}
