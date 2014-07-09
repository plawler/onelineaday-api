# Projects schema

# --- !Ups

CREATE TABLE projectz (
    id uuid primary key,
    user_id uuid,
    name varchar(255),
    description varchar(500),
    created_on timestamp,
    retired_on timestamp
);
