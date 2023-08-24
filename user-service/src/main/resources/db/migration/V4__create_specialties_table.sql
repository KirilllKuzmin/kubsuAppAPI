create table specialties (
  id serial primary key,
  name text not null unique,
  faculty_id int references faculties (id),
  degree_of_study_id int references degree_of_studies (id)
);