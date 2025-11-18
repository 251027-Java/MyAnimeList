-- Active: 1763343045633@@127.0.0.1@5432@myanimelist
-- Active: 1763343231374@@127.0.0.1@5432@postgres
-- SETUP:
-- Create a database server (docker)
-- $ docker run --name P0NawazF -e POSTGRES_USERNAME=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
-- Connect to the server (Azure Data Studio / Database extension)
-- Test your connection with a simple query (like a select)

create database myanimelist;

drop schema public;

create schema media;

create table media.anime (
    id INT PRIMARY KEY,
    title VARCHAR(30),
    genre VARCHAR(10),
    releaseDate DATE,
    totalEpisodes int,
    status VARCHAR(10)
)

