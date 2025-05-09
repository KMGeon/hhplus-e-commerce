package kr.hhplus.be.server.domain.product.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductStockDTO {
    private Long productId;
    private String productName;
    private String category;
    private String skuId;
    private Long unitPrice;
    private Long stockEa;
}