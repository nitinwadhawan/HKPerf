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
-- Dumping data for table `enum_url`
--

LOCK TABLES `enum_url` WRITE;
/*!40000 ALTER TABLE `enum_url` DISABLE KEYS */;
INSERT INTO `enum_url` VALUES (1,'www.healthkart.com'),(2,'www.snapdeal.com'),(3,'www.infibeam.com'),(4,'www.flipkart.com'),(5,'www.myntra.com'),(6,'www.jabong.com'),(7,'www.shopclues.com'),(8,'www.amazon.in');
/*!40000 ALTER TABLE `enum_url` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `response_view_type`
--

LOCK TABLES `response_view_type` WRITE;
/*!40000 ALTER TABLE `response_view_type` DISABLE KEYS */;
INSERT INTO `response_view_type` VALUES (1,'first_view'),(2,'repeat_view'),(3,'average_first_view'),(4,'average_repeat_view');
/*!40000 ALTER TABLE `response_view_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-08-19 19:26:12
