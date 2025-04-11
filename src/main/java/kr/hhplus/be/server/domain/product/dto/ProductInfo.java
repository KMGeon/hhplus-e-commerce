package kr.hhplus.be.server.domain.product.dto;


public record ProductInfo(
) {
    public record ProductInfoResponse(
            Long productId,
            String skuId,
            String category,
            String productName,
            long quantity
    ){

    }
}
