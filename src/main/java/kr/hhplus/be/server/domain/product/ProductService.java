package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.order.DatePathProvider;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.domain.vo.Ranking;
import kr.hhplus.be.server.domain.vo.RankingPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable(
            value = "product",
            key = "@cacheKeyManager.generateKey(#page, #size)",
            condition = "#page == 0"
    )
    public ProductInfo.CustomPageImpl<ProductStockDTO> getAllProduct(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductStockDTO> originalPage = productRepository.getProductsWithStockInfo(pageable);
        return new ProductInfo.CustomPageImpl<>(originalPage);
    }

    @Cacheable(
            value = "product",
            key = "@cacheKeyManager.generateKey(#categoryCode, #page, #size)",
            condition = "#page == 0"
    )
    public ProductInfo.CustomPageImpl<ProductStockDTO> getProductByCategoryCode(String categoryCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductStockDTO> originalPage = productRepository.getProductsWithStockInfoByCategory(categoryCode, pageable);
        return new ProductInfo.CustomPageImpl<>(originalPage);
    }

    @Transactional(readOnly = true)
    public void checkProductSkuIds(OrderCriteria.Item... items) {
        List<String> skuIds = Arrays.stream(items)
                .map(OrderCriteria.Item::skuId)
                .toList();
        long count = productRepository.countBySkuIdIn(skuIds);
        if (count != skuIds.size()) throw new RuntimeException("잘못된 SKU ID가 포함되어 있습니다.");
    }

    @Transactional(readOnly = true)
    public Ranking getHotProducts(String rankingPeriod, int getTopNumber) {
        RankingPeriod period = RankingPeriod.matching(rankingPeriod);
        String targetPath = DatePathProvider.toPath(LocalDateTime.now());

        return switch (period) {
            case DAILY -> productRepository.findDailyByPeriod(targetPath)
                    .getTopN(getTopNumber);
            case THREE_DAYS -> findMergeDaysRanking(targetPath, 3, period)
                    .getTopN(getTopNumber);
            case WEEKLY -> findMergeDaysRanking(targetPath, 7, period)
                    .getTopN(getTopNumber);
            default -> throw new RuntimeException("적절하지 않은 기간입니다.");
        };
    }

    private Ranking findMergeDaysRanking(String targetPath, int days, RankingPeriod period) {
        LocalDateTime baseDate = DatePathProvider.toDateTime(targetPath);
        List<Ranking> dailyRankings = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            LocalDateTime date = baseDate.minusDays(i);
            String dailyPath = DatePathProvider.toPath(date);
            Ranking dailyRanking = productRepository.findDailyByPeriod(dailyPath);

            if (dailyRanking != null && !dailyRanking.isEmpty()) {
                dailyRankings.add(dailyRanking);
            }
        }

        if (dailyRankings.isEmpty()) {
            return Ranking.empty(period, targetPath);
        }

        return Ranking.merge(period, baseDate, dailyRankings);
    }
}