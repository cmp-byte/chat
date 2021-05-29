-- script tabele
DROP TABLE user_group;
DROP TABLE messages;
DROP TABLE users;
DROP TABLE groups;
CREATE TABLE users(
    id_user INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(30) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    gender VARCHAR(1),
    birth_date DATE,
    password VARCHAR(20) NOT NULL,
    verification_code VARCHAR(30) UNIQUE,
); CREATE TABLE groups(
    id_group INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(30) NOT NULL
); CREATE TABLE user_group(
    id_ug INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_user INT UNSIGNED NOT NULL,
    id_group INT UNSIGNED NOT NULL,
    CONSTRAINT FK_id_user FOREIGN KEY(id_user) REFERENCES users(id_user) ON DELETE CASCADE,
    CONSTRAINT FK_id_groups FOREIGN KEY(id_group) REFERENCES groups(id_group) ON DELETE CASCADE,
    CONSTRAINT UQ_user_group UNIQUE(id_user,id_group)
); CREATE TABLE messages(
    id_message INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_user INT UNSIGNED NOT NULL,
    id_group INT UNSIGNED NOT NULL,
    send_date TIMESTAMP(4) DEFAULT current_timestamp(4),
    content_text VARCHAR(1000),
    attachment VARCHAR(20),
    CONSTRAINT FK_id_userM FOREIGN KEY(id_user) REFERENCES users(id_user) ON DELETE CASCADE,
    CONSTRAINT FK_id_groupsM FOREIGN KEY(id_group) REFERENCES groups(id_group) ON DELETE CASCADE
);

INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Eugen','Zorila','eugen.zorila@gmx.com','EFxCDtM7n39','M','1998-10-02');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Raluca','Rit','raluca.rit@hotmail.com','odi1rAtHTVD','F','1991-11-12');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Dorin','Sabu','dorinsabu@hotmail.com','2IQ5XKdtmqXk','M','1992-07-07');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Tereza','Schiau','tereza_schiau@protonmail.com','ypNSuY85ATjyK','F','1980-10-31');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Silviu','Podoaba','silviu_podoaba@gmail.com','kurjYirhh','M','1997-06-23');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Roxelana','Oeru','roxelana.oeru@protonmail.com','1dwK5ydnE','F','1989-06-10');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Petre','Velea','petre_velea@hotmail.com','UjBIrLQXj6K','M','1999-11-18');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Sonia','Tecuci','soniatecuci@hotmail.com','ok9P0qjN','F','1980-06-05');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Olimpian','Ruz','olimpian_ruz@protonmail.com','HJA0MYUEwh','M','1993-09-15');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Nina','Taler','nina.taler@hotmail.com','TNYmHYt3iPGpr','F','1985-01-16');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Stelian','Paje','stelianpaje@outlook.com','Uk5YoZ8eL','M','1982-11-09');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Sonia','Taler','sonia_taler@protonmail.com','md19ohVnE','F','1987-11-28');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Reghina','Cosbuc','reghina_cosbuc@gmx.com','lMW75TMNz8P','F','1984-05-25');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Adrian','Rogna','adrianrogna@outlook.com','kAPtp4XJhS','M','1985-04-28');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Dragomir','Pojoga','dragomir.pojoga@protonmail.com','XKN3P1fRO','M','1991-01-07');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Serban','Dinu','serban.dinu@protonmail.com','ot8ZeBb5mPKeO','M','1995-09-02');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Corina','Uica','corinauica@gmail.com','1vP2woSiVXJJP','F','1989-08-05');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Vlaicu','Pod','vlaicu_pod@gmail.com','nisqS4I3ijY0f','M','1985-05-10');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Ciprian','Taler','ciprian.taler@protonmail.com','XfKWhwIH','M','1996-12-15');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Stancu','Sala','stancu_sala@gmx.com','MGt3umknA','M','1987-12-03');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Suzana','Piesa','suzana_piesa@gmx.com','li4sluC9EWJ','F','1989-08-17');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Severina','Odor','severina.odor@hotmail.com','oBqjpm8YTwjtW6','F','1997-09-18');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Tatiana','Varza','tatiana.varza@hotmail.com','shBNIQZY46NcyBcGJ','F','1988-06-15');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Gabriel','Iacobescu','gabriel.iacobescu@yahoo.com','NSupHQVBi','M','1996-04-13');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Vorela','Teaca','vorela.teaca@protonmail.com','x18oY6aWv','F','1984-10-25');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Ramona','Nicolae','ramonanicolae@outlook.com','8ANHGQn0fPj','F','1992-04-22');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Florentina','Temelie','florentina_temelie@outlook.com','25LKpQViyg','F','1985-11-23');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Romi','Varvara','romi_varvara@protonmail.com','MlS8PX3fnkbg','F','1984-04-16');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Paulina','Mihalache','paulina.mihalache@gmx.com','mUTZcnj7snV','F','1984-05-05');
INSERT INTO Users(first_name,last_name,email,password,gender,birth_date) VALUES('Oliver','Voiceanu','oliver_voiceanu@yahoo.com','8XcjJpd7xSWaZ','M','1998-06-27');

