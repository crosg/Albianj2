/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50530
 Source Host           : localhost
 Source Database       : albianj

 Target Server Type    : MySQL
 Target Server Version : 50530
 File Encoding         : utf-8

 Date: 06/25/2015 11:25:32 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `ConfigItem01`
-- ----------------------------
DROP TABLE IF EXISTS `ConfigItem01`;
CREATE TABLE `ConfigItem01` (
  `Id` bigint(20) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Value` varchar(2048) DEFAULT NULL,
  `ParentId` bigint(20) NOT NULL,
  `Enable` bit(1) NOT NULL DEFAULT b'0',
  `Describe` varchar(1024) DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LastModify` timestamp NULL DEFAULT NULL,
  `Author` varchar(64) NOT NULL,
  `LastMender` varchar(64) NOT NULL,
  `IsDelete` bit(1) NOT NULL DEFAULT b'0',
  `ParentNamePath` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ConfigItem02`
-- ----------------------------
DROP TABLE IF EXISTS `ConfigItem02`;
CREATE TABLE `ConfigItem02` (
  `Id` bigint(20) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Value` varchar(2048) DEFAULT NULL,
  `ParentId` bigint(20) NOT NULL,
  `Enable` bit(1) NOT NULL DEFAULT b'0',
  `Describe` varchar(1024) DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LastModify` timestamp NULL DEFAULT NULL,
  `Author` varchar(64) NOT NULL,
  `LastMender` varchar(64) NOT NULL,
  `IsDelete` bit(1) NOT NULL DEFAULT b'0',
  `ParentNamePath` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ConfigItem03`
-- ----------------------------
DROP TABLE IF EXISTS `ConfigItem03`;
CREATE TABLE `ConfigItem03` (
  `Id` bigint(20) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Value` varchar(2048) DEFAULT NULL,
  `ParentId` bigint(20) NOT NULL,
  `Enable` bit(1) NOT NULL DEFAULT b'0',
  `Describe` varchar(1024) DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LastModify` timestamp NULL DEFAULT NULL,
  `Author` varchar(64) NOT NULL,
  `LastMender` varchar(64) NOT NULL,
  `IsDelete` bit(1) NOT NULL DEFAULT b'0',
  `ParentNamePath` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ConfigItem04`
-- ----------------------------
DROP TABLE IF EXISTS `ConfigItem04`;
CREATE TABLE `ConfigItem04` (
  `Id` bigint(20) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Value` varchar(2048) DEFAULT NULL,
  `ParentId` bigint(20) NOT NULL,
  `Enable` bit(1) NOT NULL DEFAULT b'0',
  `Describe` varchar(1024) DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LastModify` timestamp NULL DEFAULT NULL,
  `Author` varchar(64) NOT NULL,
  `LastMender` varchar(64) NOT NULL,
  `IsDelete` bit(1) NOT NULL DEFAULT b'0',
  `ParentNamePath` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ConfigItem05`
-- ----------------------------
DROP TABLE IF EXISTS `ConfigItem05`;
CREATE TABLE `ConfigItem05` (
  `Id` bigint(20) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Value` varchar(2048) DEFAULT NULL,
  `ParentId` bigint(20) NOT NULL,
  `Enable` bit(1) NOT NULL DEFAULT b'0',
  `Describe` varchar(1024) DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LastModify` timestamp NULL DEFAULT NULL,
  `Author` varchar(64) NOT NULL,
  `LastMender` varchar(64) NOT NULL,
  `IsDelete` bit(1) NOT NULL DEFAULT b'0',
  `ParentNamePath` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ConfigItem06`
-- ----------------------------
DROP TABLE IF EXISTS `ConfigItem06`;
CREATE TABLE `ConfigItem06` (
  `Id` bigint(20) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Value` varchar(2048) DEFAULT NULL,
  `ParentId` bigint(20) NOT NULL,
  `Enable` bit(1) NOT NULL DEFAULT b'0',
  `Describe` varchar(1024) DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LastModify` timestamp NULL DEFAULT NULL,
  `Author` varchar(64) NOT NULL,
  `LastMender` varchar(64) NOT NULL,
  `IsDelete` bit(1) NOT NULL DEFAULT b'0',
  `ParentNamePath` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
