/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.7.23-log : Database - Muserdb_1
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`Muserdb_1` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `Muserdb_1`;

/*Table structure for table `MUser_1` */

DROP TABLE IF EXISTS `MUser_1`;

CREATE TABLE `MUser_1` (
  `Id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `UserName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Pwd` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `MUser_2` */

DROP TABLE IF EXISTS `MUser_2`;

CREATE TABLE `MUser_2` (
  `Id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `UserName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Pwd` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `MUser_3` */

DROP TABLE IF EXISTS `MUser_3`;

CREATE TABLE `MUser_3` (
  `Id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `UserName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Pwd` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
