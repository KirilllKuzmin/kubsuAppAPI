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
api_endpoint = "/api/groups/selection?faculty=2"

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
for item in data:
    id = item["id"]
    name = item["name"]
    course = item["course"]
    faculty_id = item["faculty"]["id"]
    direction_id = item["direction"]["id"]
    direction_cipher = item["direction"]["cipher"]
    size = item["size"]

    if (direction_cipher == "02.03.03"):
        specialtyId = 2
    elif (direction_cipher == "09.03.03"):
        specialtyId = 3
    elif (direction_cipher == "01.03.02"):
        specialtyId = 4
    elif (direction_cipher == "02.03.02"):
        specialtyId = 5
    elif (direction_cipher == "09.04.02"):
        specialtyId = 6
    elif (direction_cipher == "01.04.02"):
        specialtyId = 7
    elif (direction_cipher == "02.04.02"):
        specialtyId = 8
    else:
        specialtyId = 1
    # Выполняем SQL-запрос для вставки данных
    cur.execute("""
        INSERT INTO groups (id, name, specialty_id)
        VALUES (%s, %s, %s)
    """, (id, name, specialtyId))

# Фиксируем изменения и закрываем соединение с БД
conn.commit()
cur.close()
conn.close()