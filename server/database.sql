CREATE DATABASE vibetip;
USE vibetip;

CREATE TABLE IF NOT EXISTS musicians (
	id bigint auto_increment primary key,
	name varchar(255) not null,
	location varchar(255) not null
);

CREATE TABLE IF NOT EXISTS tips (
	id bigint auto_increment primary key,
	amount double not null,
	stripe_charge_id varchar(255) not null,
    musician_id bigint not null,
	foreign key (musician_id) references musician(id)
);

CREATE TABLE IF NOT EXISTS users (
	id bigint auto_increment primary key,
	username varchar(255) unique not null,
	password varchar(255) not null,
	role varchar(50) not null
);