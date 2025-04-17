package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.*;

@Getter
@ToString
@Entity(name = "product")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseTimeEntity {

    private static final int MIN_PRODUCT_NAME_LENGTH = 2;
    private static final long MIN_PRICE = 0L;
    private static final long MAX_PRICE = 100_000_000L;

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    @Column(name = "sku_id", nullable = false)
    private String skuId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    private Long unitPrice;

    public String getCategory() {
        return this.category.getCategoryCode();
    }

    public ProductEntity updateProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public ProductEntity updateSkuId(String skuId) {
        this.skuId = skuId;
        return this;
    }

    public ProductEntity updateProductInfo(String productName, String skuId, CategoryEnum category, Long price) {
        validateCategory(category);

        this.productName = productName;
        this.skuId = skuId;
        this.category = category;
        this.unitPrice = price;

        return this;
    }

    public ProductEntity updateCategory(CategoryEnum category) {
        validateCategory(category);
        this.category = category;
        return this;
    }

    public String getCategoryCode() {
        return this.category.getCategoryCode();
    }

    public String getCategoryName() {
        return this.category.getDescription();
    }

    public ProductEntity updatePrice(Long price) {
        this.unitPrice = price;
        return this;
    }

    public ProductEntity adjustPrice(long amount) {
        long newPrice = this.unitPrice + amount;
        this.unitPrice = newPrice;
        return this;
    }

    private void validateCategory(CategoryEnum category) {
        if (category == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다");
        } else {
            CategoryEnum.getCategoryCode(category);
        }
    }
}