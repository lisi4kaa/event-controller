CREATE TABLE "event_type" (
	"id"          BIGSERIAL             PRIMARY KEY,
	"name"        VARCHAR(255)          NOT NULL,
	"category_id" BIGINT                NOT NULL REFERENCES "category"
);
CREATE TABLE "role" (
    "id"          BIGSERIAL             PRIMARY KEY,
    "name"        VARCHAR(255)          NOT NULL,
    "min"         INTEGER               NOT NULL,
    "max"         INTEGER               NOT NULL,
    "category_id" BIGINT                NOT NULL REFERENCES "category"
);
CREATE TABLE "category" (
    "id"          BIGSERIAL             PRIMARY KEY,
    "name"        VARCHAR(255)          NOT NULL,
    "parent_id"   BIGINT                NOT NULL REFERENCES "category"
);
CREATE TABLE "event" (
    "id"            BIGSERIAL             PRIMARY KEY,
	"name"          VARCHAR(255)          NOT NULL,
	"description"   TEXT                  NOT NULL,
	"event_type_id" BIGINT                NOT NULL REFERENCES "event_type",
	"start"         TIMESTAMP             NOT NULL DEFAULT now(),
	"finish"        TIMESTAMP             NOT NULL DEFAULT now()
);
CREATE TABLE "participant" (
    "id"            BIGSERIAL             PRIMARY KEY,
    "person_id"     BIGINT                NOT NULL REFERENCES "person",
    "event_id"      BIGINT                NOT NULL REFERENCES "event",
    "role_id"       BIGINT                NOT NULL REFERENCES "role"
);
CREATE TABLE "person" (
    "id"            BIGSERIAL             PRIMARY KEY,
    "fio"           VARCHAR(255)          NOT NULL
);