-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema rally_notes
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema rally_notes
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `rally_notes` DEFAULT CHARACTER SET utf8 ;
USE `rally_notes` ;

-- -----------------------------------------------------
-- Table `rally_notes`.`rally`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`rally` (
  `rally_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `date` DATE NULL,
  PRIMARY KEY (`rally_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`stage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`stage` (
  `stage_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `distance` DOUBLE NULL,
  PRIMARY KEY (`stage_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`intensity`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`intensity` (
  `intensity_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`intensity_id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`warning`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`warning` (
  `warning_id` INT NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`warning_id`),
  UNIQUE INDEX `description_UNIQUE` (`description` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`tip`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`tip` (
  `tip_id` INT NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`tip_id`),
  UNIQUE INDEX `description_UNIQUE` (`description` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`call`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`call` (
  `call_id` INT NOT NULL AUTO_INCREMENT,
  `stage_id` INT NOT NULL,
  `order_in_stage` INT NOT NULL,
  `gear` VARCHAR(45) NULL,
  `direction` VARCHAR(10) NULL,
  `intensity_id` INT NULL,
  `warning_id` INT NULL,
  `tip_id` INT NULL,
  PRIMARY KEY (`call_id`),
  INDEX `fk_call_intensity_idx` (`intensity_id` ASC) VISIBLE,
  INDEX `fk_call_warning1_idx` (`warning_id` ASC) VISIBLE,
  INDEX `fk_call_tip1_idx` (`tip_id` ASC) VISIBLE,
  INDEX `fk_call_stage1_idx` (`stage_id` ASC) VISIBLE,
  UNIQUE INDEX `stage_id_order_idx` (`stage_id` ASC, `order_in_stage` ASC) VISIBLE,
  CONSTRAINT `fk_call_intensity`
    FOREIGN KEY (`intensity_id`)
    REFERENCES `rally_notes`.`intensity` (`intensity_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_call_warning1`
    FOREIGN KEY (`warning_id`)
    REFERENCES `rally_notes`.`warning` (`warning_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_call_tip1`
    FOREIGN KEY (`tip_id`)
    REFERENCES `rally_notes`.`tip` (`tip_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_call_stage1`
    FOREIGN KEY (`stage_id`)
    REFERENCES `rally_notes`.`stage` (`stage_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`driver`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`driver` (
  `driver_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `number` INT NULL,
  PRIMARY KEY (`driver_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`manufacturer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`manufacturer` (
  `manufacturer_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`manufacturer_id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`model`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`model` (
  `model_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `manufacturer_id` INT NOT NULL,
  PRIMARY KEY (`model_id`),
  INDEX `fk_model_manufacturer1_idx` (`manufacturer_id` ASC) VISIBLE,
  CONSTRAINT `fk_model_manufacturer1`
    FOREIGN KEY (`manufacturer_id`)
    REFERENCES `rally_notes`.`manufacturer` (`manufacturer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`car`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`car` (
  `car_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `model_id` INT NULL,
  PRIMARY KEY (`car_id`),
  INDEX `fk_car_model1_idx` (`model_id` ASC) VISIBLE,
  CONSTRAINT `fk_car_model1`
    FOREIGN KEY (`model_id`)
    REFERENCES `rally_notes`.`model` (`model_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`team`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`team` (
  `team_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `driver_id` INT NULL,
  `co_driver_id` INT NULL,
  `car_id` INT NULL,
  `manufacturer_id` INT NULL,
  PRIMARY KEY (`team_id`),
  INDEX `fk_team_driver1_idx` (`driver_id` ASC) VISIBLE,
  INDEX `fk_team_driver2_idx` (`co_driver_id` ASC) VISIBLE,
  INDEX `fk_team_car1_idx` (`car_id` ASC) VISIBLE,
  INDEX `fk_team_manufacturer1_idx` (`manufacturer_id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
  CONSTRAINT `fk_team_driver1`
    FOREIGN KEY (`driver_id`)
    REFERENCES `rally_notes`.`driver` (`driver_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_driver2`
    FOREIGN KEY (`co_driver_id`)
    REFERENCES `rally_notes`.`driver` (`driver_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_car1`
    FOREIGN KEY (`car_id`)
    REFERENCES `rally_notes`.`car` (`car_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_team_manufacturer1`
    FOREIGN KEY (`manufacturer_id`)
    REFERENCES `rally_notes`.`manufacturer` (`manufacturer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`rally_to_team`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`rally_to_team` (
  `rally_to_team_id` INT NOT NULL AUTO_INCREMENT,
  `rally_id` INT NOT NULL,
  `team_id` INT NOT NULL,
  PRIMARY KEY (`rally_to_team_id`),
  INDEX `fk_rally_to_team_rally1_idx` (`rally_id` ASC) VISIBLE,
  INDEX `fk_rally_to_team_team1_idx` (`team_id` ASC) VISIBLE,
  CONSTRAINT `fk_rally_to_team_rally1`
    FOREIGN KEY (`rally_id`)
    REFERENCES `rally_notes`.`rally` (`rally_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rally_to_team_team1`
    FOREIGN KEY (`team_id`)
    REFERENCES `rally_notes`.`team` (`team_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rally_notes`.`rally_stage_map`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rally_notes`.`rally_stage_map` (
  `stage_to_rally_id` INT NOT NULL AUTO_INCREMENT,
  `stage_id` INT NOT NULL,
  `rally_id` INT NOT NULL,
  `stage_order` INT NOT NULL,
  PRIMARY KEY (`stage_to_rally_id`),
  INDEX `fk_stage_has_rally_rally1_idx` (`rally_id` ASC) VISIBLE,
  INDEX `fk_stage_has_rally_stage1_idx` (`stage_id` ASC) VISIBLE,
  UNIQUE INDEX `rally_id_stage_order` (`rally_id` ASC, `stage_order` ASC) VISIBLE,
  CONSTRAINT `fk_stage_has_rally_stage1`
    FOREIGN KEY (`stage_id`)
    REFERENCES `rally_notes`.`stage` (`stage_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_stage_has_rally_rally1`
    FOREIGN KEY (`rally_id`)
    REFERENCES `rally_notes`.`rally` (`rally_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
