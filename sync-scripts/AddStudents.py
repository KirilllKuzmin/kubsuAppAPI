import requests
import psycopg2
import os
import json

# Конфигурация подключения к БД PostgreSQL
db_host = "localhost"
db_name = "kubsuAppDB"
db_user = "postgres"
db_password = "%%%"

cur_dir=os.path.dirname(__file__)
file_path = os.path.join(cur_dir, 'students.txt')

with open(file_path, 'r', encoding='utf-8') as file:
    users = file.read().splitlines()

# Подключаемся к БД
conn = psycopg2.connect(
    host=db_host,
    dbname=db_name,
    user=db_user,
    password=db_password
)
cur = conn.cursor()

# Проходим по элементам JSON и вставляем их в БД
for index, user in enumerate(users, start=1):
    cur.execute("""
        INSERT INTO users (kubsu_user_id, username, full_name, email, password, group_id, start_education_date, end_education_date, create_date)
        VALUES (null, 's039' || %s, %s, 's039' || %s || '@mail.ru', '$2a$10$Thspx4IkWx/ujYOYBVfNyO/GqwD.9R6yGWKgyFFCT5W17iYkFcZfq', 436, null, null, CURRENT_TIMESTAMP)
    """, (index, user, index))
    conn.commit()

conn.close()