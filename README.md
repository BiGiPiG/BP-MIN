# BP-MIN

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-3.x-6DB33F.svg)](https://spring.io/projects/spring-cloud)
[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D.svg)](https://vuejs.org/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-231F20.svg)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17+-336791.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-6+-DC382D.svg)](https://redis.io/)

## Описание проекта

**BP-MIN** — это микросервисный мессенджер с возможностью обмена сообщениями в реальном времени. Проект построен с использованием технологий: **Java + Spring Boot + Spring Cloud** для бэкенда и **JavaScript + Vue 3** для фронтенда. Сервисы асинхронно взаимодействуют через **Apache Kafka** с использованием **Avro** схем для сериализации данных.

## Архитектура

Проект состоит из следующих микросервисов:

- **API Gateway** (Spring Cloud Gateway) — единая точка входа, маршрутизация запросов к соответствующим сервисам
- **Auth Service** — аутентификация и авторизация пользователей, выдача JWT токенов
- **User Service** — управление профилями пользователей
- **Chat Service** — управление чатами и сообщениями, обработка WebSocket соединений
- **Vue Client** — SPA приложение на Vue 3 (localhost:5173)

Сервисы взаимодействуют синхронно через REST API (через API Gateway) и асинхронно через Apache Kafka с использованием Avro схем.

## Технологический стек

### Бэкенд
- **<img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F.svg" height="20"/>** — фреймворк для создания веб-приложений
- **<img src="https://img.shields.io/badge/Spring%20Cloud%20Gateway-4.x-6DB33F.svg" height="20"/>** — API Gateway
- **<img src="https://img.shields.io/badge/Spring%20Security-6.0+-6DB33F.svg" height="20"/>** — защита приложения
- **<img src="https://img.shields.io/badge/JWT-Authentication-000000.svg" height="20"/>** — аутентификация и авторизация
- **<img src="https://img.shields.io/badge/WebSocket-STOMP-blue.svg" height="20"/>** — обмен сообщениями в реальном времени
- **<img src="https://img.shields.io/badge/Apache%20Kafka-3.x-231F20.svg" height="20"/>** — асинхронный обмен сообщениями между сервисами
- **<img src="https://img.shields.io/badge/Avro-Serialization-blueviolet.svg" height="20"/>** — сериализация данных для Kafka
- **<img src="https://img.shields.io/badge/JPA-Hibernate-59666C.svg" height="20"/>** — работа с базой данных
- **<img src="https://img.shields.io/badge/Maven-3.8+-C71A36.svg" height="20"/>** — система сборки

### Фронтенд
- **<img src="https://img.shields.io/badge/Vue-3.x-4FC08D.svg" height="20"/>** — прогрессивный фреймворк для создания пользовательского интерфейса
- **<img src="https://img.shields.io/badge/Vue%20Router-4.x-4FC08D.svg" height="20"/>** — маршрутизация
- **<img src="https://img.shields.io/badge/SockJS-1.x-9C27B0.svg" height="20"/>** — клиент для веб-сокетов
- **<img src="https://img.shields.io/badge/STOMP-Client-9C27B0.svg" height="20"/>** — протокол обмена сообщениями
- **<img src="https://img.shields.io/badge/Vite-4.x-646CFF.svg" height="20"/>** — сборка и разработка

### Базы данных и кэширование
- **<img src="https://img.shields.io/badge/PostgreSQL-17+-336791.svg" height="20"/>** — основная реляционная база данных
- **<img src="https://img.shields.io/badge/Redis-6+-DC382D.svg" height="20"/>** — кэширование

### Инфраструктура
- **<img src="https://img.shields.io/badge/Docker-20+-2496ED.svg" height="20"/>**
- **<img src="https://img.shields.io/badge/Docker%20Compose-2.x-2496ED.svg" height="20"/>**

## Основные функции

- **Мгновенная отправка и получение сообщений** — через WebSocket в реальном времени
- **Микросервисная архитектура** — независимое масштабирование и развертывание компонентов
- **Безопасная аутентификация через JWT** — с токенами доступа и обновления
- **Асинхронное взаимодействие сервисов** — через Kafka с Avro схемами
- **История сообщений** — все сообщения сохраняются в базе данных
- **Уведомления о новых сообщениях**
- **Профили пользователей**
- **Современный и интуитивный интерфейс** — на основе Vue 3 Composition API
- **API Gateway** — централизованная маршрутизация и безопасность

##Запуск отдельных сервисов для локальной разработки

### Шаг 1: Запуск инфраструктуры

#### Используйте docker-compose-local.yml для локальной разработки:
```bash
docker compose -f docker-compose-local.yml up -d
```

### Шаг 2: Запуск микросервисов локально
```bash
# API Gateway
cd cloud-api-gateway
./mvnw spring-boot:run

# Auth Service
cd ../auth-service
./mvnw spring-boot:run

# Chat Service
cd ../chat-service
./mvnw spring-boot:run

# User Service
cd ../user-service
./mvnw spring-boot:run
```

### Шаг 3: Запуск фронтенда
```bash
cd frontend-client
npm install
npm run dev
```

