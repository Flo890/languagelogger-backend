-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server Version:               10.3.15-MariaDB - mariadb.org binary distribution
-- Server Betriebssystem:        Win64
-- HeidiSQL Version:             10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Exportiere Daten aus Tabelle languageloggerdb.category_base_list: ~2 rows (ungefähr)
/*!40000 ALTER TABLE `category_base_list` DISABLE KEYS */;
INSERT INTO `category_base_list` (`baselist_id`, `baselist_name`, `description`, `uploaded_filename`) VALUES
	(2, 'Animals and Plants', 'All plants and animals relevant for my research project.', 'word-categories_plants-and-animals.rime'),
	(3, 'Shopping words', 'Words that express a shopping action', 'word-category-shopping.rime');
/*!40000 ALTER TABLE `category_base_list` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle languageloggerdb.logical_category_list: ~1 rows (ungefähr)
/*!40000 ALTER TABLE `logical_category_list` DISABLE KEYS */;
INSERT INTO `logical_category_list` (`logicallist_id`, `logicallist_name`, `description`, `preappy_lemma_extraction`, `active`) VALUES
	(1, 'Plants, Animals, Shopping', 'A combination of both my plants and animals list, and the shopping words.', 1, 1);
/*!40000 ALTER TABLE `logical_category_list` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle languageloggerdb.logical_category_list_category_base_list: ~2 rows (ungefähr)
/*!40000 ALTER TABLE `logical_category_list_category_base_list` DISABLE KEYS */;
INSERT INTO `logical_category_list_category_base_list` (`logical_category_list_logicallist_id`, `category_base_list_baselist_id`) VALUES
	(1, 2),
	(1, 3);
/*!40000 ALTER TABLE `logical_category_list_category_base_list` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle languageloggerdb.logical_word_list: ~1 rows (ungefähr)
/*!40000 ALTER TABLE `logical_word_list` DISABLE KEYS */;
INSERT INTO `logical_word_list` (`logicallist_id`, `logicallist_name`, `description`, `preappy_lemma_extraction`, `active`) VALUES
	(1, 'Some Stores', '', 1, 1);
/*!40000 ALTER TABLE `logical_word_list` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle languageloggerdb.logical_word_list_word_base_list: ~1 rows (ungefähr)
/*!40000 ALTER TABLE `logical_word_list_word_base_list` DISABLE KEYS */;
INSERT INTO `logical_word_list_word_base_list` (`logical_word_list_logicallist_id`, `word_base_list_baselist_id`) VALUES
	(1, 1);
/*!40000 ALTER TABLE `logical_word_list_word_base_list` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle languageloggerdb.pattern_matcher_config: ~1 rows (ungefähr)
/*!40000 ALTER TABLE `pattern_matcher_config` DISABLE KEYS */;
INSERT INTO `pattern_matcher_config` (`regex_matcher_id`, `regex_matcher_name`, `log_raw_content`, `regex`, `is_active`) VALUES
	(1, 'Emoji Matcher', 1, '[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+', 1);
/*!40000 ALTER TABLE `pattern_matcher_config` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle languageloggerdb.word: ~3 rows (ungefähr)
/*!40000 ALTER TABLE `word` DISABLE KEYS */;
INSERT INTO `word` (`id`, `word`, `word_base_list_baselist_id`) VALUES
	(1, 'Walmart', 1),
	(2, 'Amazon', 1),
	(3, 'supermarket', 1);
/*!40000 ALTER TABLE `word` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle languageloggerdb.word2category_mapping: ~13 rows (ungefähr)
/*!40000 ALTER TABLE `word2category_mapping` DISABLE KEYS */;
INSERT INTO `word2category_mapping` (`id`, `word`, `category`, `category_base_list_baselist_id`) VALUES
	(14, 'cat', 'animal', 2),
	(15, 'dog', 'animal', 2),
	(16, 'elephant', 'animal', 2),
	(17, 'fish', 'animal', 2),
	(18, 'horse', 'animal', 2),
	(19, 'tree', 'plant', 2),
	(20, 'flower', 'plant', 2),
	(21, 'grass', 'plant', 2),
	(22, 'bush', 'plant', 2),
	(23, 'buy', 'shopping', 3),
	(24, 'bought', 'shopping', 3),
	(25, 'shopping', 'shopping', 3),
	(26, 'shopping', 'shopping', 3);
/*!40000 ALTER TABLE `word2category_mapping` ENABLE KEYS */;

-- Exportiere Daten aus Tabelle languageloggerdb.word_base_list: ~1 rows (ungefähr)
/*!40000 ALTER TABLE `word_base_list` DISABLE KEYS */;
INSERT INTO `word_base_list` (`baselist_id`, `baselist_name`, `description`, `uploaded_filename`) VALUES
	(1, 'Stores', 'Some types of stores', 'dictionary-markets.rime');
/*!40000 ALTER TABLE `word_base_list` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
