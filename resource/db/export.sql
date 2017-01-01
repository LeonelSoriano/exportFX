BEGIN TRANSACTION;
CREATE TABLE `trabajo` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`nombre`	TEXT NOT NULL
);
INSERT INTO `trabajo` VALUES (1,'programador');
INSERT INTO `trabajo` VALUES (2,'arquitecto');
INSERT INTO `trabajo` VALUES (3,'plomero');
INSERT INTO `trabajo` VALUES (4,'carpintero');
INSERT INTO `trabajo` VALUES (5,'poeta');
CREATE TABLE "persona" (
	`id`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`nombre`	TEXT(50) NOT NULL,
	`alias`	TEXT,
	`cedula`	NUMERIC UNIQUE,
	`fk_trabajo`	INTEGER NOT NULL,
	FOREIGN KEY(`fk_trabajo`) REFERENCES `trabajo`(`id`)
);
INSERT INTO `persona` VALUES (1,'leonel','segundo',123,1);
INSERT INTO `persona` VALUES (2,'pedro','tercero',1234,2);
INSERT INTO `persona` VALUES (3,'ramon','cuarto',12345,3);
CREATE TABLE `no_parametro` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`name`	TEXT
);
CREATE TABLE `ejemplo` (
	`id`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`name`	TEXT
);
INSERT INTO `ejemplo` VALUES (1,'leonel');
COMMIT;
