-- Table for categories
CREATE TABLE category (
    category_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (category_id)
);

-- Table for discounts
CREATE TABLE discount (
    discount_id INT NOT NULL AUTO_INCREMENT,
    product_id INT,
    discount_percentage DECIMAL(5,2) NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME,
    PRIMARY KEY (discount_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- Table for financial information
CREATE TABLE financial (
    financial_id INT NOT NULL AUTO_INCREMENT,
    capital DECIMAL(15,2) DEFAULT 0.00,
    total_income DECIMAL(15,2) DEFAULT 0.00,
    total_cost DECIMAL(15,2) DEFAULT 0.00,
    PRIMARY KEY (financial_id)
);

-- Table for products
CREATE TABLE product (
    product_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category_id INT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    cost_price DECIMAL(10,2) NOT NULL,
    shoe_size INT,
    clothing_size INT,
    PRIMARY KEY (product_id),
    FOREIGN KEY (category_id) REFERENCES category(category_id)
);

-- Table for transactions
CREATE TABLE transaction (
    transaction_id INT NOT NULL AUTO_INCREMENT,
    product_id INT,
    transaction_type ENUM('sale','purchase') NOT NULL,
    quantity INT NOT NULL,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    price_at_transaction DECIMAL(10,2),
    PRIMARY KEY (transaction_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
