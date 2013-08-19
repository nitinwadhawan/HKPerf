-- MySQL dump 10.13  Distrib 5.1.38, for Win32 (ia32)
--
-- Host: localhost    Database: page_load_performance
-- ------------------------------------------------------
-- Server version	5.1.38-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `enum_url`
--

DROP TABLE IF EXISTS `enum_url`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enum_url` (
  `website_id` int(11) NOT NULL,
  `url` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`website_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `response`
--

DROP TABLE IF EXISTS `response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `response` (
  `request_id` int(10) NOT NULL AUTO_INCREMENT,
  `website_id` int(10) DEFAULT NULL,
  `test_id` varchar(45) DEFAULT NULL,
  `status` int(10) DEFAULT NULL,
  `runs` int(10) DEFAULT NULL,
  `create_dt` datetime DEFAULT NULL,
  `bw_Down` int(10) DEFAULT NULL,
  `bw_Up` int(10) DEFAULT NULL,
  `complete_dt` datetime DEFAULT NULL,
  `connectivity` varchar(45) DEFAULT NULL,
  `frm` varchar(45) DEFAULT NULL,
  `latency` int(10) DEFAULT NULL,
  `location` varchar(45) DEFAULT NULL,
  `view_type` int(10) DEFAULT '1',
  PRIMARY KEY (`request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `response_details`
--

DROP TABLE IF EXISTS `response_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `response_details` (
  `idresponse_details` int(10) NOT NULL AUTO_INCREMENT,
  `request_id` int(10) DEFAULT NULL,
  `response_view_type` varchar(45) DEFAULT NULL,
  `load_time` int(10) DEFAULT NULL,
  `speedIndex` varchar(45) DEFAULT NULL,
  `ttfb` varchar(45) DEFAULT NULL,
  `url` varchar(45) DEFAULT NULL,
  `visuallyCompleteDT` varchar(45) DEFAULT NULL,
  `adult_site` varchar(45) DEFAULT NULL,
  `aft` varchar(45) DEFAULT NULL,
  `avgRun` varchar(45) DEFAULT NULL,
  `base_page_cdn` varchar(45) DEFAULT NULL,
  `browser_name` varchar(45) DEFAULT NULL,
  `browser_version` varchar(45) DEFAULT NULL,
  `bytesIn` varchar(45) DEFAULT NULL,
  `bytesInDoc` varchar(45) DEFAULT NULL,
  `bytesOutDoc` varchar(45) DEFAULT NULL,
  `cached` varchar(45) DEFAULT NULL,
  `connections` varchar(45) DEFAULT NULL,
  `dt` varchar(45) DEFAULT NULL,
  `docTime` varchar(45) DEFAULT NULL,
  `domContentLoadedEventEnd` varchar(45) DEFAULT NULL,
  `domContentLoadedEventStart` varchar(45) DEFAULT NULL,
  `domElements` varchar(45) DEFAULT NULL,
  `domTime` varchar(45) DEFAULT NULL,
  `firstPaint` varchar(45) DEFAULT NULL,
  `fixed_viewport` varchar(45) DEFAULT NULL,
  `fullyLoaded` varchar(45) DEFAULT NULL,
  `gzip_savings` varchar(45) DEFAULT NULL,
  `gzip_total` varchar(45) DEFAULT NULL,
  `image_savings` varchar(45) DEFAULT NULL,
  `image_total` varchar(45) DEFAULT NULL,
  `lastVisualChange` varchar(45) DEFAULT NULL,
  `loadEventEnd` varchar(45) DEFAULT NULL,
  `loadEventStart` varchar(45) DEFAULT NULL,
  `loadTime` varchar(45) DEFAULT NULL,
  `minify_savings` varchar(45) DEFAULT NULL,
  `minify_total` varchar(45) DEFAULT NULL,
  `optimization_checked` varchar(45) DEFAULT NULL,
  `pageSpeedVersion` varchar(45) DEFAULT NULL,
  `render` varchar(45) DEFAULT NULL,
  `requests` varchar(45) DEFAULT NULL,
  `requestsDoc` varchar(45) DEFAULT NULL,
  `responses_200` varchar(45) DEFAULT NULL,
  `responses_404` varchar(45) DEFAULT NULL,
  `responses_other` varchar(45) DEFAULT NULL,
  `result` varchar(45) DEFAULT NULL,
  `score_cdn` varchar(45) DEFAULT NULL,
  `score_combine` varchar(45) DEFAULT NULL,
  `score_compress` varchar(45) DEFAULT NULL,
  `score_cookies` varchar(100) DEFAULT NULL,
  `score_etags` varchar(45) DEFAULT NULL,
  `score_gzip` varchar(45) DEFAULT NULL,
  `score_keep_alive` varchar(45) DEFAULT NULL,
  `score_minify` varchar(45) DEFAULT NULL,
  `score_progressive_jpeg` varchar(45) DEFAULT NULL,
  `server_count` varchar(45) DEFAULT NULL,
  `server_rtt` varchar(45) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `titleTime` varchar(45) DEFAULT NULL,
  `visualComplete` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idresponse_details`)
) ENGINE=InnoDB AUTO_INCREMENT=262 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `response_view_type`
--

DROP TABLE IF EXISTS `response_view_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `response_view_type` (
  `idresponse_view_type` int(11) NOT NULL,
  `view_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idresponse_view_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-08-19 15:05:34
