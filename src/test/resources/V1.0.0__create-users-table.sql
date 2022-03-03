create table users (
   id integer primary key,
   version integer not null default 1,
   name varchar(255) not null
);
