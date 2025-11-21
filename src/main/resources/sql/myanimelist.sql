-- Active: 1763343045633@@127.0.0.1@5432@myanimelist
-- Active: 1763343231374@@127.0.0.1@5432@postgres
-- SETUP:
-- Create a database server (docker)
-- $ docker run --name P0NawazF -e POSTGRES_USERNAME=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
-- Connect to the server (Azure Data Studio / Database extension)
-- Test your connection with a simple query (like a select)

create database myanimelist;

create schema myanimelist;

create table myanimelist.anime (
    anime_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(100),
    total_episodes int,
    status VARCHAR(10)
    avg_rating DOUBLE PRECISION
)

create table myanimelist.user (
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50)
)

create table myanimelist.user_anime_watched (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL,
    anime_id INTEGER NOT NULL,
    watched BOOLEAN
);