package io.dedyn.hy.watchworldshop.entities;

import lombok.Getter;

@Getter
public enum SectionType {
    PRODUCT("Sản phẩm", "product"),
    BRAND("Hãng", "brand"),
    CATEGORY("Loại", "category");

    private final String displayValue;
    private final String path;

    SectionType(String displayValue, String path) {
        this.displayValue = displayValue;
        this.path = path;
    }

}
