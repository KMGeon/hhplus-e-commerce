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


    public String getCategoryCode() {
        return this.category.getCategoryCode();
    }

}