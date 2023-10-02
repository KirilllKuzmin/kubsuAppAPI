import requests
import psycopg2
import json

# Конфигурация подключения к БД PostgreSQL
db_host = "localhost"
db_name = "kubsuAppDB"
db_user = "postgres"
db_password = "%"

# URL для GET-запроса и API-эндпоинт
api_url = "https://api.cube.nlabs.su"
api_endpoint = "/api/teachers/selection"

# Выполняем GET-запрос и получаем JSON-данные
response = requests.get(api_url + api_endpoint)
data = response.json()

# Подключаемся к БД
conn = psycopg2.connect(
    host=db_host,
    dbname=db_name,
    user=db_user,
    password=db_password
)
cur = conn.cursor()

# Проходим по элементам JSON и вставляем их в БД
for index, item in enumerate(data, start=1):
    full_name = item["full_name"]
    specialtyId = 1
    # Выполняем SQL-запрос для вставки данных
    cur.execute("""
        INSERT INTO users (kubsu_user_id, username, full_name, email, password, group_id, start_education_date, end_education_date, create_date)
        VALUES (null, 'p001' || %s, %s, 'p001' || %s || '@mail.ru', 'test', 1, null, null, CURRENT_TIMESTAMP)
    """, (index, full_name, index))
    conn.commit()

    cur.execute("""
        INSERT INTO user_roles (user_id, role_id)
        VALUES (%s, 2)
    """, [index])

# Фиксируем изменения и закрываем соединение с БД
conn.commit()
cur.close()
conn.close()