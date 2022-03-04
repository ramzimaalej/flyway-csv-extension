create table users (
   id BINARY(16) not null primary key,
   version integer not null default 1,
   name varchar(255) not null
);
