import requests
import psycopg2
import json

db_host = "localhost"
db_name = "kubsuAppDB"
db_user = "postgres"
db_password = "%"

api_url = "https://api.cube.nlabs.su"
api_endpoint = "/api/timetable/main_lessons/viewer?group="

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

cur.execute("""
        select id from groups where id <> 1;
    """)
groups = [row[0] for row in cur.fetchall()]

for group in groups:

    response = requests.get(api_url + api_endpoint + str(group))
    data = response.json()

    items = data["data"]

    # Проходим по элементам JSON и вставляем их в БД
    for item in items:
        weekday = item["weekday"]
        parity = item["parity"]
        number = item["number"]
        type = item["type"]
        discipline = item["discipline"]
        place = item["place"]
        teachers = item["teachers"]

        if type is not None:
            type_id = type["id"]
            type_name = type["name"]
        else:
            type_id = 0
            type_name = "Не определено"

        if discipline is not None:
            discipline_name = discipline["name"]
        else:
            discipline_name = "Не определено"

        if place is not None:
            place_name = place["name"]
        else:
            place_name = "Не определена"

        cur.execute("SELECT id FROM accounting_schema.classrooms WHERE classroom_number = %s", (place_name,))
        existing_classrooms = cur.fetchone()
        if not existing_classrooms:
            cur.execute("""
                        INSERT INTO accounting_schema.classrooms (classroom_number, academic_building_id)
                        VALUES (%s, 1)
                        RETURNING id
                    """, (place_name,))
            classroom_number = cur.fetchone()
        else:
            cur.execute("""
                        select id from accounting_schema.classrooms where classroom_number = %s
                    """, (place_name,))
            classroom_number = cur.fetchone()



        cur.execute("SELECT id FROM accounting_schema.course_types WHERE name = %s", (type_name,))
        existing_course_type = cur.fetchone()
        if not existing_course_type:
            cur.execute("""
                        INSERT INTO accounting_schema.course_types (name)
                        VALUES (%s)
                        RETURNING id
                    """, (type_name,))
            type_id = cur.fetchone()
        else:
            cur.execute("""
                        select id from accounting_schema.course_types where name = %s
                    """, (type_name,))
            type_id = cur.fetchone()



        cur.execute("SELECT id FROM accounting_schema.courses WHERE name = %s AND course_type_id = %s", (discipline_name,type_id))
        existing_course_name = cur.fetchone()
        if not existing_course_name:
            cur.execute("""
                        INSERT INTO accounting_schema.courses (name, course_type_id)
                        VALUES (%s, %s)
                        RETURNING id
                    """, (discipline_name, type_id))
            course_id = cur.fetchone()
        else:
            cur.execute("""
                        select id from accounting_schema.courses where name = %s AND course_type_id = %s
                    """, (discipline_name,type_id))
            course_id = cur.fetchone()



        for teacher in teachers:
            full_name = teacher["full_name"]

            cur.execute("SELECT id FROM users WHERE full_name = %s", (full_name,))
            user_id = cur.fetchone()

            cur.execute("SELECT id FROM accounting_schema.lecturers WHERE user_id = %s", (user_id,))
            existing_teacher = cur.fetchone()

            if not existing_teacher:
                cur.execute("""
                        INSERT INTO accounting_schema.lecturers (user_id, academic_degree_id)
                        VALUES (%s, 1)
                        RETURNING id
                    """, (user_id,))

                lecturer_id = cur.fetchone()
            else:
                cur.execute("""
                        select id from accounting_schema.lecturers where user_id = %s
                    """, (user_id,))

                lecturer_id = cur.fetchone()

        if parity is not None:
            parity += 1
        if parity == 2:
            partity = 1
        elif parity == 1:
            partity = 2

        if number != 0:
            cur.execute("""
                    INSERT INTO accounting_schema.timetables (classroom_id, lecturer_id, course_id, day_of_week, num_time_class_id, week_type_id, semester_id)
                    VALUES (%s, %s, %s, %s, %s, %s, %s)
                    RETURNING id
                """, (classroom_number, lecturer_id, course_id, weekday + 1, number, parity, 1))
            timetable_id = cur.fetchone()

        cur.execute("""
                    INSERT INTO accounting_schema.timetable_groups (timetable_id, group_id)
                    VALUES (%s, %s)
                """, (timetable_id, group))

# Фиксируем изменения и закрываем соединение с БД
conn.commit()
cur.close()
conn.close()