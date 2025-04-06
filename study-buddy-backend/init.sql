CREATE USER IF NOT EXISTS 'studyadmin'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
GRANT ALL PRIVILEGES ON studybuddy.* TO 'studyadmin'@'%';
FLUSH PRIVILEGES;
