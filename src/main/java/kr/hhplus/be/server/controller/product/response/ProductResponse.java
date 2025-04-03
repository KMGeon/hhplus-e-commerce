package kr.hhplus.be.server.controller.product.response;

public record ProductResponse(
        long productId,
        String productName,
        long productPrice,
        long stockQuantity
) {
}
