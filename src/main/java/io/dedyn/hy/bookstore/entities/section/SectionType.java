package io.dedyn.hy.bookstore.entities.section;

import lombok.Getter;

@Getter
public enum SectionType {
    BOOK("Sách", "book"),
    PUBLISHER("Nhà xuất bản", "publisher"),
    CATEGORY("Loại", "category"),
    RECOMMENDATION("Đề xuất", "recommendation");

    private final String displayValue;
    private final String path;

    SectionType(String displayValue, String path) {
        this.displayValue = displayValue;
        this.path = path;
    }

}
