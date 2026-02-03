# BP-MIN

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D.svg)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17+-336791.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-6+-DC382D.svg)](https://redis.io/)

## 📖 Описание проекта

**BP-MIN** — это реализация мессенджера с возможностью обмена сообщениями в реальном времени. Проект построен с использованием современных технологий: **Java + Spring Boot** для бэкенда и **JavaScript + Vue 3** для фронтенда.

## 🛠️ Технологический стек

### Бэкенд
- **<img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F.svg" height="20"/>** — фреймворк для создания веб-приложений
- **<img src="https://img.shields.io/badge/Spring%20Security-6.0+-6DB33F.svg" height="20"/>** — защита приложения
- **<img src="https://img.shields.io/badge/JWT-Authentication-000000.svg" height="20"/>** — аутентификация и авторизация
- **<img src="https://img.shields.io/badge/WebSocket-STOMP-blue.svg" height="20"/>** — обмен сообщениями в реальном времени
- **<img src="https://img.shields.io/badge/JPA-Hibernate-59666C.svg" height="20"/>** — работа с базой данных
- **<img src="https://img.shields.io/badge/Maven-3.8+-C71A36.svg" height="20"/>** — система сборки

### Фронтенд
- **<img src="https://img.shields.io/badge/Vue-3.x-4FC08D.svg" height="20"/>** — прогрессивный фреймворк для создания пользовательского интерфейса
- **<img src="https://img.shields.io/badge/Vue%20Router-4.x-4FC08D.svg" height="20"/>** — маршрутизация
- **<img src="https://img.shields.io/badge/SockJS-1.x-9C27B0.svg" height="20"/>** — клиент для веб-сокетов
- **<img src="https://img.shields.io/badge/STOMP-Client-9C27B0.svg" height="20"/>** — протокол обмена сообщениями
- **<img src="https://img.shields.io/badge/Vite-4.x-646CFF.svg" height="20"/>** — сборка и разработка

### Базы данных и кэширование
- **<img src="https://img.shields.io/badge/PostgreSQL-14+-336791.svg" height="20"/>** — основная реляционная база данных
- **<img src="https://img.shields.io/badge/Redis-6+-DC382D.svg" height="20"/>** — кэширование

### Инфраструктура
- **<img src="https://img.shields.io/badge/Docker-20+-2496ED.svg" height="20"/>**
- **<img src="https://img.shields.io/badge/Docker%20Compose-2.x-2496ED.svg" height="20"/>**

## ✨ Основные функции

- 📱 **Мгновенная отправка и получение сообщений** — через WebSocket в реальном времени
- 🔐 **Безопасная аутентификация через JWT** — с токенами доступа и обновления
- 📁 **История сообщений** — все сообщения сохраняются в базе данных
- 🔔 **Уведомления о новых сообщениях**
- 👤 **Профили пользователей**
- 🎨 **Современный и интуитивный интерфейс** — на основе Vue 3 Composition API

## 🐳 Установка и апуск через Docker Compose (рекомендуется)

### Требования
- Java 17+
- Node.js 16+
- PostgreSQL 17+
- Redis 6+
- Maven 3.8+

### 1. Клонирование репозитория

```bash

git clone https://github.com/yourusername/bp-min.git
cd bp-min
```

### 2. Настройка конфигурации

Откройте файл ./server/src/main/resources/application.yml и замените значение поля secret_key на свой секретный ключ

### 3. Сборка сервера

```bash

cd ./server
./mvnw clean package -DskipTests
```

### 4. Запуск контейнеров

```bash

cd ../
docker compose up
```
### Доступ к приложению
- После запуска приложение будет доступно по адресу:
- Фронтенд: http://localhost:8080
- Бэкенд API: http://localhost:8081/api
- WebSocket: ws://localhost:8081/ws
