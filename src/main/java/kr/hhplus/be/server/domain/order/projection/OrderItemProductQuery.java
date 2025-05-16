package kr.hhplus.be.server.domain.order.projection;

public interface OrderItemProductQuery {
    Long getOrderItemId();
    String getSkuId();
    Long getEa();
    Long getUnitPrice();
    String getProductName();

    default Long getTotalPrice() {
        return getUnitPrice() * getEa();
    }
}