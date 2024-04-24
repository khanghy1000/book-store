CREATE TABLE administrative_regions (
    id           INTEGER      NOT NULL,
    "name"       VARCHAR(255) NOT NULL,
    name_en      VARCHAR(255) NOT NULL,
    code_name    VARCHAR(255) NULL,
    code_name_en VARCHAR(255) NULL,
    CONSTRAINT administrative_regions_pkey PRIMARY KEY (id)
);

CREATE TABLE administrative_units (
    id            INTEGER      NOT NULL,
    full_name     VARCHAR(255) NULL,
    full_name_en  VARCHAR(255) NULL,
    short_name    VARCHAR(255) NULL,
    short_name_en VARCHAR(255) NULL,
    code_name     VARCHAR(255) NULL,
    code_name_en  VARCHAR(255) NULL,
    CONSTRAINT administrative_units_pkey PRIMARY KEY (id)
);

CREATE TABLE provinces (
    code                     VARCHAR(20)  NOT NULL,
    "name"                   VARCHAR(255) NOT NULL,
    name_en                  VARCHAR(255) NULL,
    full_name                VARCHAR(255) NOT NULL,
    full_name_en             VARCHAR(255) NULL,
    code_name                VARCHAR(255) NULL,
    administrative_unit_id   INTEGER      NULL,
    administrative_region_id INTEGER      NULL,
    CONSTRAINT provinces_pkey PRIMARY KEY (code)
);

ALTER TABLE provinces
    ADD CONSTRAINT provinces_administrative_region_id_fkey FOREIGN KEY (administrative_region_id) REFERENCES administrative_regions (id);
ALTER TABLE provinces
    ADD CONSTRAINT provinces_administrative_unit_id_fkey FOREIGN KEY (administrative_unit_id) REFERENCES administrative_units (id);

CREATE INDEX idx_provinces_region ON provinces (administrative_region_id);
CREATE INDEX idx_provinces_unit ON provinces (administrative_unit_id);

CREATE TABLE districts (
    code                   VARCHAR(20)  NOT NULL,
    "name"                 VARCHAR(255) NOT NULL,
    name_en                VARCHAR(255) NULL,
    full_name              VARCHAR(255) NULL,
    full_name_en           VARCHAR(255) NULL,
    code_name              VARCHAR(255) NULL,
    province_code          VARCHAR(20)  NULL,
    administrative_unit_id INTEGER      NULL,
    CONSTRAINT districts_pkey PRIMARY KEY (code)
);

ALTER TABLE districts
    ADD CONSTRAINT districts_administrative_unit_id_fkey FOREIGN KEY (administrative_unit_id) REFERENCES administrative_units (id);
ALTER TABLE districts
    ADD CONSTRAINT districts_province_code_fkey FOREIGN KEY (province_code) REFERENCES provinces (code);

CREATE INDEX idx_districts_province ON districts (province_code);
CREATE INDEX idx_districts_unit ON districts (administrative_unit_id);

CREATE TABLE wards (
    code                   VARCHAR(20)  NOT NULL,
    "name"                 VARCHAR(255) NOT NULL,
    name_en                VARCHAR(255) NULL,
    full_name              VARCHAR(255) NULL,
    full_name_en           VARCHAR(255) NULL,
    code_name              VARCHAR(255) NULL,
    district_code          VARCHAR(20)  NULL,
    administrative_unit_id INTEGER      NULL,
    CONSTRAINT wards_pkey PRIMARY KEY (code)
);

ALTER TABLE wards
    ADD CONSTRAINT wards_administrative_unit_id_fkey FOREIGN KEY (administrative_unit_id) REFERENCES administrative_units (id);
ALTER TABLE wards
    ADD CONSTRAINT wards_district_code_fkey FOREIGN KEY (district_code) REFERENCES districts (code);

CREATE INDEX idx_wards_district ON wards (district_code);
CREATE INDEX idx_wards_unit ON wards (administrative_unit_id);


CREATE TABLE users (
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name        VARCHAR(255)            NOT NULL,
    last_name         VARCHAR(255)            NOT NULL,
    email             VARCHAR(255) UNIQUE     NOT NULL,
    password          VARCHAR(68)             NOT NULL,
    image             VARCHAR(255),
    enabled           BOOLEAN   DEFAULT FALSE NOT NULL,
    created_at        TIMESTAMP DEFAULT NOW() NOT NULL,
    verification_code VARCHAR(64)
);

CREATE TABLE roles (
    id   SMALLINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE users_roles (
    user_id BIGINT,
    role_id SMALLINT,

    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE categories (
    id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) UNIQUE NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE brands (
    id   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) UNIQUE NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    logo VARCHAR(255)
);

CREATE TABLE products (
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(255) UNIQUE   NOT NULL,
    slug              VARCHAR(255) UNIQUE   NOT NULL,
    main_image        VARCHAR(255),
    short_description VARCHAR,
    full_description  TEXT,
    quantity          INT                   NOT NULL,
    price             FLOAT                 NOT NULL,
    discount_percent  FLOAT   DEFAULT 0     NOT NULL,
    shipping_fee      FLOAT                 NOT NULL,
    brand_id          INT                   NOT NULL,
    enabled           BOOLEAN DEFAULT FALSE NOT NULL,
    created_at        TIMESTAMP             NOT NULL,
    updated_at        TIMESTAMP             NOT NULL,

    FOREIGN KEY (brand_id) REFERENCES brands (id)
);

CREATE TABLE products_categories (
    product_id  BIGINT NOT NULL,
    category_id INT    NOT NULL,

    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    PRIMARY KEY (product_id, category_id)
);

CREATE TABLE product_images (
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    file_name  VARCHAR(255) NOT NULL,
    product_id BIGINT       NOT NULL,

    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE product_specs (
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name       VARCHAR(150) NOT NULL,
    value      VARCHAR(255) NOT NULL,
    product_id BIGINT       NOT NULL,

    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE shipping_rates (
    id            INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    district_code VARCHAR(20) NOT NULL,
    rate          FLOAT       NOT NULL,

    FOREIGN KEY (district_code) REFERENCES districts (code)
);

CREATE TABLE shipping_info (
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15)  NOT NULL,
    address_line VARCHAR(255) NOT NULL,
    ward_code    VARCHAR(20)  NOT NULL,
    customer_id  BIGINT       NOT NULL,
    is_default   BOOLEAN      NOT NULL,

    FOREIGN KEY (ward_code) REFERENCES wards (code),
    FOREIGN KEY (customer_id) REFERENCES users (id)
);

CREATE TABLE cart_items (
    customer_id BIGINT   NOT NULL,
    product_id  BIGINT   NOT NULL,
    quantity    SMALLINT NOT NULL,

    FOREIGN KEY (customer_id) REFERENCES users (id),
    FOREIGN KEY (product_id) REFERENCES products (id),
    PRIMARY KEY (customer_id, product_id)
);

CREATE TYPE STATUS AS ENUM ('Đã đặt', 'Đang giao', 'Đã giao', 'Đã huỷ');

CREATE TABLE orders (
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    customer_id   BIGINT       NOT NULL,
    order_time    TIMESTAMP    NOT NULL,
    shipping_cost FLOAT        NOT NULL,
    total         FLOAT        NOT NULL,
    status        STATUS       NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(15)  NOT NULL,
    address_line  VARCHAR(255) NOT NULL,
    ward          VARCHAR(255) NOT NULL,
    district      VARCHAR(255) NOT NULL,
    province      VARCHAR(255) NOT NULL,
    deliver_time  TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES users (id)
);

CREATE TABLE order_details (
    order_id   BIGINT   NOT NULL,
    product_id BIGINT   NOT NULL,
    quantity   SMALLINT NOT NULL,
    unit_price FLOAT    NOT NULL,

    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES products (id),
    PRIMARY KEY (order_id, product_id)
);