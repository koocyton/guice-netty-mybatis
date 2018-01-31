-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: 2018-01-29 15:20:24
-- 服务器版本： 5.6.25
-- PHP Version: 7.0.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `wx_wolf`
--

-- --------------------------------------------------------

--
-- 表的结构 `account`
--

CREATE TABLE `account` (
  `id` bigint(18) UNSIGNED NOT NULL COMMENT '主 ID',
  `platform` enum('local','weixin','facebook','weibo','twitter') NOT NULL COMMENT '第三方平台',
  `platform_uid` varchar(100) NOT NULL COMMENT '第三方平台 ID',
  `verified` tinyint(1) NOT NULL COMMENT '邮箱是否验证通过的账号，第三方登陆，默认通过(1)',
  `account` varchar(250) NOT NULL COMMENT '邮箱或第三方 ID',
  `password` varchar(32) NOT NULL COMMENT '密码 md5(manager + '' & '' + source_password)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账号';

--
-- 转存表中的数据 `account`
--

INSERT INTO `account` (`id`, `platform`, `platform_uid`, `verified`, `account`, `password`, `created_at`) VALUES
  (957995474248404992, 'weixin', 'weixin_on7310Gxw-hpARQhIdREIUIAeC9Q', 1, 'weixin_on7310Gxw-hpARQhIdREIUIAeC9Q', '', '2018-01-29 15:14:54');

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE `user` (
  `id` bigint(18) UNSIGNED NOT NULL COMMENT '主 ID',
  `nickname` varchar(250) NOT NULL COMMENT '昵称',
  `gender` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别',
  `avatar_url` text NOT NULL COMMENT '头像',
  `country` char(2) NOT NULL COMMENT '国家',
  `friends` text NOT NULL COMMENT '好友 ID',
  `score` int(11) NOT NULL COMMENT '得分',
  `ranking` int(11) NOT NULL COMMENT '排名',
  `gold` int(11) NOT NULL COMMENT '金币'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户';

--
-- 转存表中的数据 `user`
--

INSERT INTO `user` (`id`, `nickname`, `gender`, `avatar_url`, `country`, `friends`, `score`, `ranking`, `gold`) VALUES
  (957995474248404992, '刘毅', 1, 'https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJt18m6r8LJW6xWDvFGYeyeJyweebK46YwGc0zDts02f8ggUg3szAhf9rDFBpPpWkGOoeCIoADnuw/0', 'CN', '', 0, 0, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `platform_uid` (`platform`,`platform_uid`) USING BTREE;

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);
COMMIT;
