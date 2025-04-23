-- Создание таблицы Car
CREATE TABLE car (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    cost NUMERIC(12, 2) NOT NULL CHECK (cost > 0)
);

-- Создание таблицы Person
CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL CHECK (age > 0),
    has_driver_license BOOLEAN NOT NULL DEFAULT FALSE,
    car_id BIGINT REFERENCES car(id)
);

-- Создание индекса для улучшения производительности JOIN-запросов
CREATE INDEX idx_person_car_id ON person(car_id);