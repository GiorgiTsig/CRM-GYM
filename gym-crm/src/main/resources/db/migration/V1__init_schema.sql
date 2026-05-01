CREATE TABLE trainee
(
    id            BINARY(16)   NOT NULL,
    date_of_birth date NULL,
    address       VARCHAR(255) NULL,
    user_id       BINARY(16)   NULL,
    CONSTRAINT pk_trainee PRIMARY KEY (id)
);

CREATE TABLE trainer
(
    id                BINARY(16) NOT NULL,
    specialization_id BINARY(16) NOT NULL,
    user_id           BINARY(16) NOT NULL,
    CONSTRAINT pk_trainer PRIMARY KEY (id)
);

CREATE TABLE trainer_trainee
(
    trainee_id BINARY(16) NOT NULL,
    trainer_id BINARY(16) NOT NULL
);

CREATE TABLE training
(
    id               BINARY(16)   NOT NULL,
    trainee_id       BINARY(16)   NULL,
    trainer_id       BINARY(16)   NULL,
    name             VARCHAR(100) NOT NULL,
    training_type_id BINARY(16)   NULL,
    date             date         NOT NULL,
    duration         INT          NOT NULL,
    CONSTRAINT pk_training PRIMARY KEY (id)
);

CREATE TABLE training_type
(
    id                 BINARY(16)   NOT NULL,
    training_type_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_training_type PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                    BINARY(16)    NOT NULL,
    first_name            VARCHAR(255)  NOT NULL,
    last_name             VARCHAR(255)  NOT NULL,
    username              VARCHAR(255)  NOT NULL,
    password              VARCHAR(255)  NOT NULL,
    is_active             BIT(1)        NOT NULL,
    last_logout           datetime NULL,
    failed_login_attempts INT DEFAULT 0 NOT NULL,
    lock_until            datetime NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE trainee
    ADD CONSTRAINT uc_trainee_user UNIQUE (user_id);

ALTER TABLE trainer
    ADD CONSTRAINT uc_trainer_user UNIQUE (user_id);

ALTER TABLE training_type
    ADD CONSTRAINT uc_training_type_trainingtypename UNIQUE (training_type_name);

ALTER TABLE users
    ADD CONSTRAINT uc_user_username UNIQUE (username);

ALTER TABLE trainee
    ADD CONSTRAINT FK_TRAINEE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE trainer
    ADD CONSTRAINT FK_TRAINER_ON_SPECIALIZATION FOREIGN KEY (specialization_id) REFERENCES training_type (id);

ALTER TABLE trainer
    ADD CONSTRAINT FK_TRAINER_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE training
    ADD CONSTRAINT FK_TRAINING_ON_TRAINEE FOREIGN KEY (trainee_id) REFERENCES trainee (id);

ALTER TABLE training
    ADD CONSTRAINT FK_TRAINING_ON_TRAINER FOREIGN KEY (trainer_id) REFERENCES trainer (id);

ALTER TABLE training
    ADD CONSTRAINT FK_TRAINING_ON_TRAININGTYPE FOREIGN KEY (training_type_id) REFERENCES training_type (id);

ALTER TABLE trainer_trainee
    ADD CONSTRAINT fk_tratra_on_trainee FOREIGN KEY (trainee_id) REFERENCES trainee (id);

ALTER TABLE trainer_trainee
    ADD CONSTRAINT fk_tratra_on_trainer FOREIGN KEY (trainer_id) REFERENCES trainer (id);