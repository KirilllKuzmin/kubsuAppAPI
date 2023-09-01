create table user_roles (
    user_id int references users (id),
    role_id int references roles (id)
);