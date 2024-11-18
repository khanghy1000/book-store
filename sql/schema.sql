CREATE TABLE provinces (
    code         VARCHAR(20)  NOT NULL,
    "name"       VARCHAR(255) NOT NULL,
    name_en      VARCHAR(255) NULL,
    full_name    VARCHAR(255) NOT NULL,
    full_name_en VARCHAR(255) NULL,
    code_name    VARCHAR(255) NULL,
    CONSTRAINT provinces_pkey PRIMARY KEY (code)
);

CREATE TABLE districts (
    code          VARCHAR(20)  NOT NULL,
    "name"        VARCHAR(255) NOT NULL,
    name_en       VARCHAR(255) NULL,
    full_name     VARCHAR(255) NULL,
    full_name_en  VARCHAR(255) NULL,
    code_name     VARCHAR(255) NULL,
    province_code VARCHAR(20)  NULL,
    CONSTRAINT districts_pkey PRIMARY KEY (code)
);

ALTER TABLE districts
    ADD CONSTRAINT districts_province_code_fkey FOREIGN KEY (province_code) REFERENCES provinces (code);

CREATE INDEX idx_districts_province ON districts (province_code);

CREATE TABLE wards (
    code          VARCHAR(20)  NOT NULL,
    "name"        VARCHAR(255) NOT NULL,
    name_en       VARCHAR(255) NULL,
    full_name     VARCHAR(255) NULL,
    full_name_en  VARCHAR(255) NULL,
    code_name     VARCHAR(255) NULL,
    district_code VARCHAR(20)  NULL,
    CONSTRAINT wards_pkey PRIMARY KEY (code)
);

ALTER TABLE wards
    ADD CONSTRAINT wards_district_code_fkey FOREIGN KEY (district_code) REFERENCES districts (code);

CREATE INDEX idx_wards_district ON wards (district_code);

CREATE TABLE roles (
    id   SMALLINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE users (
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name        VARCHAR(255)            NOT NULL,
    last_name         VARCHAR(255)            NOT NULL,
    email             VARCHAR(255) UNIQUE     NOT NULL,
    password          VARCHAR(68)             NOT NULL,
    role_id           SMALLINT                NOT NULL,
    image             VARCHAR(255),
    enabled           BOOLEAN   DEFAULT FALSE NOT NULL,
    created_at        TIMESTAMP DEFAULT NOW() NOT NULL,
    verification_code VARCHAR(64),

    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE categories (
    id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) UNIQUE NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE publishers (
    id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) UNIQUE NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    logo VARCHAR(255)
);

CREATE TABLE books (
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(255) UNIQUE   NOT NULL,
    slug              VARCHAR(255) UNIQUE   NOT NULL,
    main_image        VARCHAR(255),
    short_description VARCHAR,
    full_description  TEXT,
    age_limit         INT                   NOT NULL,
    page_count        INT                   NOT NULL,
    published_at      DATE                  NOT NULL,
    language          VARCHAR(50)           NOT NULL,
    quantity          INT                   NOT NULL,
    price             FLOAT                 NOT NULL,
    discount_percent  FLOAT   DEFAULT 0     NOT NULL,
    shipping_fee      FLOAT                 NOT NULL,
    publisher_id      INT                   NOT NULL,
    enabled           BOOLEAN DEFAULT FALSE NOT NULL,
    created_at        TIMESTAMP             NOT NULL,
    updated_at        TIMESTAMP             NOT NULL,

    FOREIGN KEY (publisher_id) REFERENCES publishers (id)
);

CREATE TABLE books_authors (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    book_id     BIGINT       NOT NULL,
    author_name VARCHAR(255) NOT NULL,

    FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE books_categories (
    book_id     BIGINT NOT NULL,
    category_id INT    NOT NULL,

    FOREIGN KEY (book_id) REFERENCES books (id),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    PRIMARY KEY (book_id, category_id)
);

CREATE TABLE book_images (
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    file_name VARCHAR(255) NOT NULL,
    book_id   BIGINT       NOT NULL,

    FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE book_specs (
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name    VARCHAR(150) NOT NULL,
    value   VARCHAR(255) NOT NULL,
    book_id BIGINT       NOT NULL,

    FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE cart_items (
    customer_id BIGINT NOT NULL,
    book_id     BIGINT NOT NULL,
    quantity    INT    NOT NULL,

    FOREIGN KEY (customer_id) REFERENCES users (id),
    FOREIGN KEY (book_id) REFERENCES books (id),
    PRIMARY KEY (customer_id, book_id)
);

CREATE TYPE ORDER_STATUS AS ENUM ('ORDERED', 'SHIPPING', 'DELIVERED', 'CANCELLED');

CREATE TABLE orders (
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    customer_id   BIGINT       NOT NULL,
    order_time    TIMESTAMP    NOT NULL,
    items_price   FLOAT        NOT NULL,
    shipping_cost FLOAT        NOT NULL,
    total         FLOAT        NOT NULL,
    status        ORDER_STATUS NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(15)  NOT NULL,
    address_line  VARCHAR(255) NOT NULL,
    ward          VARCHAR(255) NOT NULL,
    district      VARCHAR(255) NOT NULL,
    province      VARCHAR(255) NOT NULL,
    deliver_time  TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES users (id)
);

CREATE TABLE order_details (
    order_id   BIGINT NOT NULL,
    book_id    BIGINT NOT NULL,
    quantity   INT    NOT NULL,
    unit_price FLOAT  NOT NULL,

    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (book_id) REFERENCES books (id),
    PRIMARY KEY (order_id, book_id)
);

CREATE TYPE SECTION_TYPE AS ENUM ('BOOK', 'CATEGORY', 'PUBLISHER', 'RECOMMENDATION');

CREATE TABLE sections (
    id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title   VARCHAR(255)          NOT NULL,
    enabled BOOLEAN DEFAULT FALSE NOT NULL,
    "order" INT                   NOT NULL,
    type    SECTION_TYPE          NOT NULL

);

CREATE TABLE sections_books (
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    book_id    BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    "order"    INT    NOT NULL,

    FOREIGN KEY (book_id) REFERENCES books (id),
    FOREIGN KEY (section_id) REFERENCES sections (id)
);

CREATE TABLE sections_categories (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category_id INT    NOT NULL,
    section_id  BIGINT NOT NULL,
    "order"     INT    NOT NULL,

    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (section_id) REFERENCES sections (id)
);

CREATE TABLE sections_publishers (
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    publisher_id INT    NOT NULL,
    section_id   BIGINT NOT NULL,
    "order"      INT    NOT NULL,

    FOREIGN KEY (publisher_id) REFERENCES publishers (id),
    FOREIGN KEY (section_id) REFERENCES sections (id)
);