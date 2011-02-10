-- you may use this to create a table for messages in your database ready2go with JdbcMessageProvider

CREATE TABLE `Message` (
    `basename` VARCHAR( 31 ) NOT NULL ,
    `language` VARCHAR( 7 ) NULL ,
    `country` VARCHAR( 7 ) NULL ,
    `variant` VARCHAR( 7 ) NULL ,
    `key` VARCHAR( 255 ) NULL ,
    `message` TEXT NULL
);

