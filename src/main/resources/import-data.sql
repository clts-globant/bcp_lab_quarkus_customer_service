-- Sample accounts for development
INSERT INTO customers (id, name, documentId, email, status) VALUES
(101, 'Penny Q', '1011', 'pq@mail.com', 'ACTIVE'),
(102, 'Dime Street', '1012', 'ds@mail.com', 'ACTIVE'),
(103, 'Quarter Venegas', '1013', 'qv@mail.com', 'ACTIVE'),
(104, 'Nickel Sohr', '1014', 'ns@correo.co', 'INACTIVE');

-- Set sequence for auto-increment ID
SELECT setval('customers_seq', 105, true);
