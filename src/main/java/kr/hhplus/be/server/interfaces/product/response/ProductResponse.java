package kr.hhplus.be.server.interfaces.product.response;

public record ProductResponse(
        long productId,
        String productName,
        long productPrice,
        long stockQuantity
) {
}
