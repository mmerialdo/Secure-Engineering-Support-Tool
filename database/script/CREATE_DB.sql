CREATE DATABASE sest;
CREATE USER 'sest'@'localhost' IDENTIFIED BY 'sest';
CREATE USER 'sest'@'127.0.0.1' IDENTIFIED BY 'sest';
CREATE USER 'sest'@'%' IDENTIFIED BY 'sest';
GRANT ALL PRIVILEGES ON sest.* TO 'sest'@'localhost' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON sest.* TO 'sest'@'127.0.0.1' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON sest.* TO 'sest'@'%' WITH GRANT OPTION;
USE sest;
source SEST_DDL.sql;
source SEST_DDL_encrypt.sql;
source SEST_prefill.sql;
source SEST_DDL Fill data.sql;
source SEST_prefill_AUDIT.sql;

--mysql -h localhost -P 3306 -u sest -p -D sest;
--SHOW DATABASES;
--SELECT user, host FROM mysql.user;
