package kr.hhplus.be.server.domain.product.projection;

public interface ProductStockDTO {
    Long getProductId();
    String getProductName();
    String getCategory();
    String getSkuId();
    Long getUnitPrice();
    Long getStockEa();
}