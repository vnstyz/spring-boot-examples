-- Employee table schema restored from entity `Employee`
-- Target database: MySQL 8.x

CREATE TABLE IF NOT EXISTS `employee` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `last_name` VARCHAR(30) NOT NULL COMMENT 'Employee name',
  `email` VARCHAR(60) NOT NULL COMMENT 'Email address',
  `gender` TINYINT NOT NULL COMMENT 'Gender: 1 male, 0 female',
  `department` INT NOT NULL COMMENT 'Department code',
  `birth` DATE NOT NULL COMMENT 'Birth date',
  PRIMARY KEY (`id`),
  KEY `idx_employee_department` (`department`),
  KEY `idx_employee_birth` (`birth`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
