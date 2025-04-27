package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private static final int MAX_RETRY = 5;
    private static final long BACKOFF_INITIAL_MS = 100L;
    private static final String NAMED_LOCK_KEY = "stock_lock_";

    public List<StockInfo.Stock> checkEaAndProductInfo(StockCommand.Order stockCommand) {
        StockInventory requestInventory = StockInventory.fromStock(stockCommand);

        List<EnoughStockDTO> stockList = stockRepository.findSkuIdAndAvailableEa(requestInventory.getSkuIds());

        StockInventory availableInventory = StockInventory.fromStockData(stockList);

        availableInventory.validateAgainst(requestInventory);

        return stockList.stream()
                .map(v1 -> new StockInfo.Stock(v1.getSkuId(), v1.getEa(), v1.getUnitPrice()))
                .toList();
    }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public int decreaseStockPessimistic(final Long createOrderId, StockCommand.Order stockCommand) {
            int cnt = 0;
            for (StockCommand.Order.Item item : stockCommand.items()) {
                cnt += stockRepository.updateStockDecreaseFifo(
                        createOrderId,
                        item.skuId(),
                        item.ea()
                );
            }
            return cnt;
        }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int decreaseStock(final Long createOrderId, StockCommand.Order stockCommand) {
        int cnt = 0;
        for (StockCommand.Order.Item item : stockCommand.items()) {
            String lockKey = NAMED_LOCK_KEY + item.skuId();
            int retryCount = 0;

            while (retryCount < MAX_RETRY) {
                boolean lockAcquired = false;
                try {
                    // 1. 락 획득 시도
                    Integer lockResult = stockRepository.getLock(lockKey);
                    if (lockResult == 1) {
                        lockAcquired = true;

                        // 2. 재고 차감 실행
                        int updated = stockRepository.updateStockDecreaseFifo(
                                createOrderId,
                                item.skuId(),
                                item.ea()
                        );

                        // 3. 업데이트 결과 확인
                        if (updated == item.ea()) {
                            // 요청한 수량만큼 정확히 차감됨 - 성공
                            cnt += updated;
                            break;
                        } else if (updated > 0) {
                            // 일부만 차감됨 (재고 부족) - 실패로 처리하고 롤백
                            log.warn("재고 부족: skuId={}, 요청수량={}, 가용수량={}",
                                    item.skuId(), item.ea(), updated);
                            throw new RuntimeException("재고 부족: " + item.skuId());
                        } else {
                            // 전혀 차감되지 않음 (재고 없음)
                            log.warn("재고 없음: skuId={}, 요청수량={}",
                                    item.skuId(), item.ea());
                            throw new RuntimeException("재고 없음: " + item.skuId());
                        }
                    } else {
                        // 락 획득 실패 - 재시도
                        log.warn("락 획득 실패: skuId={}, 시도={}/{}",
                                item.skuId(), retryCount + 1, MAX_RETRY);
                        retryCount++;

                        if (retryCount >= MAX_RETRY) {
                            throw new RuntimeException("락 획득 최대 시도 횟수 초과: " + item.skuId());
                        }

                        // 지수 백오프 적용 후 재시도
                        applyBackoff(retryCount);
                        continue;
                    }
                } catch (Exception e) {
                    // 재고 없음/부족은 재시도하지 않고 바로 실패 처리
                    if (e.getMessage() != null &&
                            (e.getMessage().startsWith("재고 없음") ||
                                    e.getMessage().startsWith("재고 부족"))) {
                        throw e;
                    }

                    log.warn("재고 차감 처리 중 오류 발생: skuId={}, 시도={}/{}, 오류={}",
                            item.skuId(), retryCount + 1, MAX_RETRY, e.getMessage());
                    retryCount++;

                    if (retryCount >= MAX_RETRY)
                        throw new RuntimeException("재고 차감 최대 시도 횟수 초과: " + item.skuId(), e);

                    applyBackoff(retryCount);
                }
                /** 락을 획득했다면 반드시 해제 **/
                finally {

                    if (lockAcquired) {
                        try {
                            stockRepository.releaseLock(lockKey);
                        } catch (Exception e) {
                            log.error("락 해제 중 오류 발생: key={}, 오류={}", lockKey, e.getMessage());
                        }
                    }
                }
            }
        }
        return cnt;
    }

    private void applyBackoff(int retryCount) {
        try {
            long backoffMs = BACKOFF_INITIAL_MS * (long) Math.pow(2, retryCount);
            Thread.sleep(backoffMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("재고 차감 재시도 중 인터럽트 발생", ie);
        }
    }

    public void restoreStock(Long orderId) {
        stockRepository.restoreStockByOrderId(orderId);
    }

}
