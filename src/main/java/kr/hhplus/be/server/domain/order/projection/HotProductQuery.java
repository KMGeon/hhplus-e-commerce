package kr.hhplus.be.server.domain.order.projection;

import kr.hhplus.be.server.domain.product.CategoryEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HotProductQuery {
    private String skuId;
    private String category;
    private String productName;
    private Long orderCount;

    public HotProductQuery(String skuId, CategoryEnum category, String productName, Long orderCount) {
        this.skuId = skuId;
        this.category = category.getCategoryCode();
        this.productName = productName;
        this.orderCount = orderCount;
    }
}
