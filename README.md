# BP-MIN

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen.svg)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-6+-red.svg)](https://redis.io/)

## Описание проекта

**BP-MIN** - это реализация мессенджера с возможностью обмена сообщениями в реальном времени, построенный с использованием java + spring и js + vue.

## Технологический стек

### Бэкенд
- **Java 17+** - основной язык программирования
- **Spring Boot** - фреймворк для создания веб-приложений
- **Spring Security** - защита приложения
- **JWT** - аутентификация и авторизация
- **WebSocket + STOMP** - обмен сообщениями в реальном времени
- **JPA/Hibernate** - работа с базой данных

### Фронтенд
- **Vue 3** - прогрессивный фреймворк для создания пользовательского интерфейса
- **Vue Router** - маршрутизация
- **SockJS + STOMP** - клиент для веб-сокетов

### Базы данных и кэширование
- **PostgreSQL** - основная реляционная база данных
- **Redis** - кэширование и сессии

## Основные функции

- 📱 Мгновенная отправка и получение сообщений
- 🔐 Безопасная аутентификация через JWT
- 📁 История сообщений
- 🔔 Уведомления о новых сообщениях
- 🎨 Современный и интуитивный интерфейс

## Установка и запуск

### Требования
- Java 17+
- Node.js 16+
- PostgreSQL 14+
- Redis 6+
- Maven 3.8+

### 1. Клонирование репозитория

```bash
git clone https://github.com/yourusername/bp-min.git
cd bp-min
```

### 2. Сборка сервера

```bash
cd ./server
./mvnw clean package -DskipTests
```

### 3. Запуск контейнеров

```bash
cd ../
docker compose up
```
