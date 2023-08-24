create table users (
    id serial primary key,
    kubsu_user_id int,
    username text not null,
    full_name text not null,
    email text not null,
    password text,
    group_id int references groups (id),
    start_education_date timestamp,
    end_education_date timestamp,
    create_date timestamp
);
