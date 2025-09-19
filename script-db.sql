-- Database: cine-backend

-- DROP DATABASE IF EXISTS "cine-backend";

CREATE DATABASE "cine-backend"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en-US'
    LC_CTYPE = 'en-US'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- SCHEMA: cine_auth

-- DROP SCHEMA IF EXISTS cine_auth ;

CREATE SCHEMA IF NOT EXISTS cine_auth
    AUTHORIZATION postgres;



-- SCHEMA: cine_movie

-- DROP SCHEMA IF EXISTS cine_movie ;

CREATE SCHEMA IF NOT EXISTS cine_movie
    AUTHORIZATION postgres;
	
	
	
-- SCHEMA: cine_booking

-- DROP SCHEMA IF EXISTS cine_booking ;

CREATE SCHEMA IF NOT EXISTS cine_booking
    AUTHORIZATION postgres;
	
	