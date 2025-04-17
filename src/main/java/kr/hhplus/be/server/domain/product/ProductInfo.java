package kr.hhplus.be.server.domain.product;

public class ProductInfo {
    public record SelectProductInfo(String skuId, String categoryCode, String name, Long unitPrice, Long stockEa){
        public static SelectProductInfo of(ProductEntity product, long ea) {
            return new SelectProductInfo(product.getSkuId(), product.getCategoryCode(), product.getProductName(), product.getUnitPrice(), ea);
        }
    }
}
