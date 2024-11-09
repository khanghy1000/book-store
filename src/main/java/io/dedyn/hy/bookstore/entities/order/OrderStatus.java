package io.dedyn.hy.bookstore.entities.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDERED("Đã đặt"),
    SHIPPING("Đang giao"),
    DELIVERED("Đã giao"),
    CANCELLED("Đã hủy")
;
private final String displayValue;

    OrderStatus(String displayValue) {
        this.displayValue = displayValue;
    }

}
