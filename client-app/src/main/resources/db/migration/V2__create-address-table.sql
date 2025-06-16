
CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(20),
    postcode VARCHAR(20),
    city VARCHAR(100) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    FOREIGN KEY (customer_id)
       REFERENCES customers(id)
       ON DELETE CASCADE
);
