CREATE TABLE orders (
    id UUID PRIMARY KEY,
    location_x INT NOT NULL,
    location_y INT NOT NULL,
    volume INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    courier_id UUID
);

CREATE TABLE couriers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    speed INT NOT NULL,
    location_x INT NOT NULL,
    location_y INT NOT NULL
);

CREATE TABLE courier_storage_places (
    id UUID PRIMARY KEY,
    courier_id UUID REFERENCES couriers(id),
    name VARCHAR(255) NOT NULL,
    total_volume INT NOT NULL,
    order_id UUID
);
