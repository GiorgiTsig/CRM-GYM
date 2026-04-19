CREATE TABLE trainer
(
    id               UUID         NOT NULL,
    trainer_username VARCHAR(255) NOT NULL,
    first_name       VARCHAR(255),
    last_name        VARCHAR(255),
    active           BOOLEAN,
    CONSTRAINT pk_trainer PRIMARY KEY (id)
);

CREATE TABLE trainer_month
(
    id              UUID NOT NULL,
    month_value     INT,
    total_duration  INT  NOT NULL,
    trainer_year_id UUID NOT NULL,
    CONSTRAINT pk_trainer_month PRIMARY KEY (id)
);

CREATE TABLE trainer_year
(
    id         UUID NOT NULL,
    year_value INT,
    trainer_id UUID NOT NULL,
    CONSTRAINT pk_trainer_year PRIMARY KEY (id)
);

ALTER TABLE trainer_month
    ADD CONSTRAINT uc_51d7442bc7448a2ac551a46df UNIQUE (trainer_year_id, month_value);

ALTER TABLE trainer_year
    ADD CONSTRAINT uc_fe6b5620c1cfe04517e8ea759 UNIQUE (trainer_id, year_value);

ALTER TABLE trainer
    ADD CONSTRAINT uc_trainer_trainerusername UNIQUE (trainer_username);

ALTER TABLE trainer_month
    ADD CONSTRAINT FK_TRAINER_MONTH_ON_TRAINER_YEAR FOREIGN KEY (trainer_year_id) REFERENCES trainer_year (id);

ALTER TABLE trainer_year
    ADD CONSTRAINT FK_TRAINER_YEAR_ON_TRAINER FOREIGN KEY (trainer_id) REFERENCES trainer (id);