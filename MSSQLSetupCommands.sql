CREATE DATABASE QuickFoodMS;

CREATE TABLE drivers (
	id INT PRIMARY KEY IDENTITY (1, 1), 
	name VARCHAR(50),
	surname VARCHAR(50), 
	city VARCHAR(50),
	current_load INT,
);

CREATE TABLE customers (
	id INT PRIMARY KEY IDENTITY (1, 1),
	name VARCHAR(50),
	surname VARCHAR(50),
	phone VARCHAR(20),
	delivery_address VARCHAR(100),
	email VARCHAR(50),
	city VARCHAR(50)
);

CREATE TABLE restaurants (
	id INT PRIMARY KEY IDENTITY (1, 1),
	name VARCHAR(50),
	city VARCHAR(50),
	phone VARCHAR(10),
);

CREATE TABLE orders (
	id INT PRIMARY KEY IDENTITY (1, 1),
	driver_id INT FOREIGN KEY REFERENCES drivers(id),
	customer_id INT FOREIGN KEY REFERENCES customers(id),
	restaurant_id INT FOREIGN KEY REFERENCES restaurants(id),
	total_cost DECIMAL(7,2),
	order_status VARCHAR(30),
	completion_date_time DATETIME
);

CREATE TABLE items_ordered (
	id INT PRIMARY KEY IDENTITY (1, 1),
	orders_id INT FOREIGN KEY REFERENCES orders(id),
	item_name VARCHAR(100),
	cost DECIMAL(7,2),
	qty INT,
	special_instructions VARCHAR(100),
	items_total_cost DECIMAL(7, 2)
);