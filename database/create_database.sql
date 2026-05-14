-- ==========================================
-- SCRIPT SQL PARA CREAR BASE DE DATOS
-- Proyecto GAMEXUS - MySQL 8.0+
-- ==========================================

CREATE DATABASE IF NOT EXISTS gamexus
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gamexus;

SELECT 'Base de datos gamexus creada exitosamente' AS mensaje;

-- Las tablas se crean automáticamente por Hibernate al arrancar Spring Boot.
-- Configuración esperada: spring.jpa.hibernate.ddl-auto=update
-- Las semillas iniciales se cargan desde DataInitializer.java.