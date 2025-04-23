-- Добавление ограничений для таблицы Student
ALTER TABLE student
ADD CONSTRAINT age_check CHECK (age >= 16),
ADD CONSTRAINT name_unique UNIQUE (name),
ALTER COLUMN name SET NOT NULL;

-- Установка значения по умолчанию для возраста
ALTER TABLE student
ALTER COLUMN age SET DEFAULT 20;

-- Добавление ограничения для таблицы Faculty
ALTER TABLE faculty
ADD CONSTRAINT name_color_unique UNIQUE (name, color);