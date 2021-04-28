-- script tabele
DROP TABLE chat.user_group;
DROP TABLE chat.messages;
DROP TABLE chat.users;
DROP TABLE chat.groups;
CREATE TABLE chat.users(
    id_user INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(30) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    email VARCHAR(50) NOT NULL,
    gender VARCHAR(1),
    birth_date DATE,
    password VARCHAR(20) NOT NULL
); CREATE TABLE chat.groups(
    id_group INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(30) NOT NULL
); CREATE TABLE chat.user_group(
    id_ug INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_user INT UNSIGNED NOT NULL,
    id_group INT UNSIGNED NOT NULL,
    CONSTRAINT FK_id_user FOREIGN KEY(id_user) REFERENCES chat.users(id_user) ON DELETE CASCADE,
    CONSTRAINT FK_id_groups FOREIGN KEY(id_group) REFERENCES chat.groups(id_group) ON DELETE CASCADE,
    CONSTRAINT UQ_user_group UNIQUE(id_user,id_group)
); CREATE TABLE chat.messages(
    id_message INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_user INT UNSIGNED NOT NULL,
    id_group INT UNSIGNED NOT NULL,
    send_date TIMESTAMP(4) DEFAULT current_timestamp(4),
    content_text VARCHAR(1000),
    attachment VARCHAR(20),
    CONSTRAINT FK_id_userM FOREIGN KEY(id_user) REFERENCES chat.users(id_user) ON DELETE CASCADE,
    CONSTRAINT FK_id_groupsM FOREIGN KEY(id_group) REFERENCES chat.groups(id_group) ON DELETE CASCADE
);