package kr.hhplus.be.server.domain.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class ProductInfo {
    public record SelectProductInfo(String skuId, String categoryCode, String name, Long unitPrice, Long stockEa){
        public static SelectProductInfo of(ProductEntity product, long ea) {
            return new SelectProductInfo(product.getSkuId(), product.getCategoryCode(), product.getProductName(), product.getUnitPrice(), ea);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
    public static class CustomPageImpl<T> extends PageImpl<T> {

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CustomPageImpl(@JsonProperty("content") List<T> content,
                              @JsonProperty("number") int page,
                              @JsonProperty("size") int size,
                              @JsonProperty("totalElements") long total) {
            super(content, PageRequest.of(page, size), total);
        }

        public CustomPageImpl(Page<T> page) {
            super(page.getContent(), page.getPageable(), page.getTotalElements());
        }
    }
}