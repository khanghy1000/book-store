package io.dedyn.hy.watchworldshop.entities;

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

    public String getDisplayValue() {
        return displayValue;
    }
}
