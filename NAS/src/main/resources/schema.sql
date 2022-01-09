CREATE TABLE IF NOT EXISTS `nas`.`users` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(1000) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `enable` TINYINT(1) NOT NULL,
    `last_login` DATETIME NULL,
    `last_modification` DATETIME NULL DEFAULT NOW(),
    `active_since` DATETIME NULL,
    `dark_theme` TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
    ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `nas`.`roles` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `role` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    CONSTRAINT `user_id`
        FOREIGN KEY (`user_id`)
            REFERENCES `nas`.`users` (`id`))
    ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `nas`.`verification_tokens` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `token` VARCHAR(255) NOT NULL,
    `operation` VARCHAR(255) NOT NULL,
    `user_id` INT(11) NOT NULL,
    `expiry_date` DATETIME NOT NULL,
    `used` TINYINT(1) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    INDEX `user_id` (`user_id` ASC) VISIBLE,
    CONSTRAINT `user_id_verification_token`
    FOREIGN KEY (`user_id`)
    REFERENCES `nas`.`users` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `nas`.`files` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `path` VARCHAR(500) NOT NULL,
    `size` INT NOT NULL,
    `is_folder` TINYINT(1) NOT NULL,
    `user_id` INT NOT NULL,
    `created` DATETIME NOT NULL,
    `modified` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    UNIQUE INDEX `user_path_UNIQUE` (`path` ASC, `user_id` ASC) VISIBLE,
    INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
    CONSTRAINT `fk_user_id`
        FOREIGN KEY (`user_id`)
            REFERENCES `nas`.`users` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION);

CREATE OR REPLACE VIEW `users_view` AS select u.*, r.roles, ifnull(files.size, 0) as size from users u
inner join (select user_id, group_concat(role separator ', ') as roles from roles group by user_id) r
on u.id = r.user_id
left join (select user_id, sum(size) as size from files group by user_id) as files
on files.user_id = r.user_id;

CREATE TABLE IF NOT EXISTS `nas`.`parameters` (
                                    `id` INT NOT NULL AUTO_INCREMENT,
                                    `name` VARCHAR(200) NOT NULL,
                                    `value` VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (`id`),
                                    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;

INSERT IGNORE INTO `nas`.`parameters` (`name`, `value`) VALUES ('mail.user', 'REPLACE_ME_WITH_USERNAME');
INSERT IGNORE INTO `nas`.`parameters` (`name`, `value`) VALUES ('mail.password', 'REPLACE_ME_WITH_PASSWORD');