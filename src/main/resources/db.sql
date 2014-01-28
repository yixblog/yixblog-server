-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.5.32-MariaDB - mariadb.org binary distribution
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  8.0.0.4396
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出 yixblog 的数据库结构
DROP DATABASE IF EXISTS `yixblog`;
CREATE DATABASE IF NOT EXISTS `yixblog` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `yixblog`;


-- 导出  表 yixblog.blog_accounts 结构
DROP TABLE IF EXISTS `blog_accounts`;
CREATE TABLE IF NOT EXISTS `blog_accounts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(30) DEFAULT NULL,
  `pwd` varchar(32) DEFAULT NULL,
  `nick` varchar(30) NOT NULL,
  `avatar` varchar(45) DEFAULT NULL,
  `qq` varchar(11) DEFAULT NULL,
  `weibo` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `banflag` tinyint(1) DEFAULT '0',
  `regtime` bigint(20) NOT NULL,
  `lastlogin` bigint(20) DEFAULT NULL,
  `sex` varchar(1) NOT NULL DEFAULT '',
  `temp_email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nick_UNIQUE` (`nick`),
  UNIQUE KEY `uid_UNIQUE` (`uid`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `qq_UNIQUE` (`qq`),
  UNIQUE KEY `weibo_UNIQUE` (`weibo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 yixblog.blog_admins 结构
DROP TABLE IF EXISTS `blog_admins`;
CREATE TABLE IF NOT EXISTS `blog_admins` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(30) NOT NULL,
  `pwd` varchar(32) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `regtime` bigint(20) NOT NULL,
  `lastlogin` bigint(20) NOT NULL,
  `temp_email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid_UNIQUE` (`uid`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 yixblog.blog_admin_character 结构
DROP TABLE IF EXISTS `blog_admin_character`;
CREATE TABLE IF NOT EXISTS `blog_admin_character` (
  `admin` int(10) unsigned NOT NULL,
  `character` int(10) unsigned NOT NULL,
  PRIMARY KEY (`admin`,`character`),
  KEY `FK_ADMIN_CHARACTER_ADMIN_idx` (`admin`),
  KEY `FK_ADMIN_CHARACTER_CHARACTER_idx` (`character`),
  CONSTRAINT `FK_ADMIN_CHARACTER_ADMIN` FOREIGN KEY (`admin`) REFERENCES `blog_admins` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ADMIN_CHARACTER_CHARACTER` FOREIGN KEY (`character`) REFERENCES `blog_characters` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  视图 yixblog.blog_admin_permission_view 结构
DROP VIEW IF EXISTS `blog_admin_permission_view`;
-- 创建临时表以解决视图依赖性错误
CREATE TABLE `blog_admin_permission_view` (
	`id` INT(10) UNSIGNED NOT NULL,
	`uid` VARCHAR(30) NOT NULL COLLATE 'utf8_general_ci',
	`pwd` VARCHAR(32) NOT NULL COLLATE 'utf8_general_ci',
	`email` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`regtime` BIGINT(20) NOT NULL,
	`lastlogin` BIGINT(20) NOT NULL,
	`temp_email` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`system_config` TINYINT(1) NULL,
	`user_manage` TINYINT(1) NULL,
	`admin_manage` TINYINT(1) NULL,
	`article_manage` TINYINT(1) NULL,
	`comment_manage` TINYINT(1) NULL
) ENGINE=MyISAM;


-- 导出  表 yixblog.blog_articles 结构
DROP TABLE IF EXISTS `blog_articles`;
CREATE TABLE IF NOT EXISTS `blog_articles` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `content` text NOT NULL,
  `addtime` bigint(20) NOT NULL,
  `edittime` bigint(20) DEFAULT NULL,
  `author` int(10) unsigned NOT NULL,
  `topflag` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `IK_ARTICLE_TITLE` (`title`),
  KEY `FK_ARTICLE_AUTHOR_idx` (`author`),
  KEY `IK_ARTICLE_ADDTIME` (`addtime`),
  CONSTRAINT `FK_ARTICLE_AUTHOR` FOREIGN KEY (`author`) REFERENCES `blog_accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 yixblog.blog_characters 结构
DROP TABLE IF EXISTS `blog_characters`;
CREATE TABLE IF NOT EXISTS `blog_characters` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `system_config` tinyint(1) DEFAULT '0',
  `user_manage` tinyint(1) DEFAULT '0',
  `admin_manage` tinyint(1) DEFAULT '0',
  `article_manage` tinyint(1) DEFAULT '0',
  `comment_manage` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 yixblog.blog_comments 结构
DROP TABLE IF EXISTS `blog_comments`;
CREATE TABLE IF NOT EXISTS `blog_comments` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(45) DEFAULT NULL,
  `content` text NOT NULL,
  `author` int(10) unsigned NOT NULL,
  `article` int(10) unsigned NOT NULL,
  `addtime` bigint(20) NOT NULL,
  `floor` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_COMMENTS_AUTHOR_idx` (`author`),
  KEY `FK_COMMENTS_ARTICLE_idx` (`article`),
  CONSTRAINT `FK_COMMENTS_ARTICLE` FOREIGN KEY (`article`) REFERENCES `blog_articles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_COMMENTS_AUTHOR` FOREIGN KEY (`author`) REFERENCES `blog_accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 yixblog.blog_images 结构
DROP TABLE IF EXISTS `blog_images`;
CREATE TABLE IF NOT EXISTS `blog_images` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(100) NOT NULL,
  `account` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_USER_IMAGES` (`account`),
  CONSTRAINT `FK_USER_IMAGES` FOREIGN KEY (`account`) REFERENCES `blog_accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 yixblog.blog_notice 结构
DROP TABLE IF EXISTS `blog_notice`;
CREATE TABLE IF NOT EXISTS `blog_notice` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(30) NOT NULL,
  `content` text,
  `addtime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 yixblog.blog_tags 结构
DROP TABLE IF EXISTS `blog_tags`;
CREATE TABLE IF NOT EXISTS `blog_tags` (
  `article` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tag` varchar(45) NOT NULL,
  PRIMARY KEY (`article`,`tag`),
  KEY `IK_TAGS` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 yixblog.blog_timertask 结构
DROP TABLE IF EXISTS `blog_timertask`;
CREATE TABLE IF NOT EXISTS `blog_timertask` (
  `code` varchar(50) NOT NULL,
  `addtime` bigint(20) NOT NULL,
  `overtime` bigint(20) NOT NULL,
  `type` int(11) NOT NULL,
  `data` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  视图 yixblog.blog_admin_permission_view 结构
DROP VIEW IF EXISTS `blog_admin_permission_view`;
-- 移除临时表并创建最终视图结构
DROP TABLE IF EXISTS `blog_admin_permission_view`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` VIEW `yixblog`.`blog_admin_permission_view` AS select `a`.`id` AS `id`,`a`.`uid` AS `uid`,`a`.`pwd` AS `pwd`,`a`.`email` AS `email`,`a`.`regtime` AS `regtime`,`a`.`lastlogin` AS `lastlogin`,`a`.`temp_email` AS `temp_email`,max(`c`.`system_config`) AS `system_config`,max(`c`.`user_manage`) AS `user_manage`,max(`c`.`admin_manage`) AS `admin_manage`,max(`c`.`article_manage`) AS `article_manage`,max(`c`.`comment_manage`) AS `comment_manage` from ((`blog_admins` `a` left join `blog_admin_character` `ac` on((`ac`.`admin` = `a`.`id`))) left join `blog_characters` `c` on((`c`.`id` = `ac`.`character`))) group by `a`.`id` ;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
