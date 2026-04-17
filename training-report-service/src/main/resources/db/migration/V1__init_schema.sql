CREATE TABLE trainer_summary
(
    id               BINARY(16) NOT NULL,
    trainer_username VARCHAR(255),
    first_name       VARCHAR(255),
    last_name        VARCHAR(255),
    active           BOOLEAN,
    year_value           INT    NOT NULL,
    month_value      INT    NOT NULL,
    total_duration   INT    NOT NULL,
    CONSTRAINT pk_trainer_summary PRIMARY KEY (id)
);