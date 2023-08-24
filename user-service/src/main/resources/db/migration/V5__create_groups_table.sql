create table groups (
  id serial primary key,
  name text not null unique,
  specialty_id int references specialties (id)
);