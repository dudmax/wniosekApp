CREATE TABLE States
(
	id   BIGINT NOT NULL,
	name VARCHAR(255),
	PRIMARY KEY (id)
);

CREATE TABLE Applications
(
	id          BIGINT NOT NULL AUTO_INCREMENT,
	title       VARCHAR(255),
	description VARCHAR(255),
	state_id    BIGINT,
	PRIMARY KEY (id),
	FOREIGN KEY (state_id) REFERENCES States (id)
);

CREATE TABLE Application_Changes
(
	id             BIGINT NOT NULL AUTO_INCREMENT,
	application_id BIGINT,
	title          VARCHAR(255),
	description    VARCHAR(255),
	state_id       BIGINT,
	reason         VARCHAR(255),
	change_date    TIMESTAMP,
	PRIMARY KEY (id),
	FOREIGN KEY (application_id) REFERENCES Applications (id),
	FOREIGN KEY (state_id) REFERENCES States (id)
);


