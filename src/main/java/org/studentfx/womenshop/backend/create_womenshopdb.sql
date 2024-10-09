CREATE DATABASE IF NOT EXISTS WomenShopDB;

USE WomenShopDB;

CREATE TABLE Category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE Product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category_id INT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    cost_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

CREATE TABLE Transaction (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    transaction_type ENUM('sale', 'purchase') NOT NULL,
    quantity INT NOT NULL,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    price_at_transaction DECIMAL(10, 2),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

CREATE TABLE Discount (
    discount_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    discount_percentage DECIMAL(5, 2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME,
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

CREATE TABLE Financial (
    financial_id INT AUTO_INCREMENT PRIMARY KEY,
    capital DECIMAL(15, 2) DEFAULT 0,
    total_income DECIMAL(15, 2) DEFAULT 0,
    total_cost DECIMAL(15, 2) DEFAULT 0
);
