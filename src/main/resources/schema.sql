-- drop table users cascade;
-- drop table items cascade;
-- drop table bookings cascade;
-- drop table comments cascade ;
-- drop table item_requests cascade;
-- drop type status_booking;
--
--
-- CREATE TYPE status_booking AS ENUM ('APPROVED', 'CANCELED', 'REJECTED', 'WAITING');
-- CREATE TYPE role AS ENUM ('ROLE_USER', 'ROLE_MODERATOR');

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(64) NOT NULL,
    city VARCHAR(256) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(127) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT pk_items PRIMARY KEY (item_id),
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS bookings (
     booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     item_id BIGINT NOT NULL,
     booker_id BIGINT NOT NULL,
     status status_booking,
    CONSTRAINT pk_bookings PRIMARY KEY (booking_id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(user_id),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(item_id)
    );
CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (comment_id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(user_id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(item_id)
    );

CREATE TABLE IF NOT EXISTS item_requests (
    item_request_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(512) NOT NULL,
    requestor_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_item_requests PRIMARY KEY (item_request_id),
    CONSTRAINT fk_item_request_to_users FOREIGN KEY(requestor_id) REFERENCES users(user_id)
    );

