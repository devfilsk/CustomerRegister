
CREATE TABLE phones(
    id UUID PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    FOREIGN KEY (customer_id)
       REFERENCES customers(id)
       ON DELETE CASCADE
);