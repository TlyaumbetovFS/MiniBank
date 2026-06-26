# Minibank

Мини-банк на Spring Boot: регистрация, авторизация, переводы, платежи по шаблону.

## Стек

- **Backend:** Java 21, Spring Boot 4, Spring Data JPA / Hibernate
- **БД:** PostgreSQL 18
- **Документация API:** Swagger UI (springdoc-openapi)
- **Мониторинг:** Actuator -> Prometheus -> Grafana
- **Контейнеризация:** Docker
- **Нагрузка:** Apache JMeter + InfluxDB -> Grafana

## Запуск

```bash
docker compose up -d --build
docker compose ps          # все сервисы должны быть Up
```

Проверка:

```bash
curl localhost:8080/actuator/health   # {"status":"UP"}
```

## Порты

| Сервис | URL | Доступ                     |
|--------|-----|----------------------------|
| Приложение | http://localhost:8080 | API                        |
| Swagger UI | http://localhost:8080/swagger-ui.html | документация API           |
| Grafana | http://localhost:3000 | admin / admin              |
| Prometheus | http://localhost:9090 | -                          |
| InfluxDB | http://localhost:8086 | база `jmeter`              |
| PostgreSQL | localhost:5433 | bank / bank, БД `minibank` |

## API

Операции:

- **Регистрация:** `POST /api/registration/{init,verify,complete}` — создаёт пользователя и счёт (стартовый баланс 1000)
- **Авторизация:** `POST /api/auth/login/{init,confirm}` — вход по телефону, возвращает токен
- **Переводы:** `POST /api/transfers/{init,confirm,execute}` — внутренние (INTERNAL) и внешние (EXTERNAL)
- **Платежи по шаблону:** `POST /api/payments/{template,init,confirm,execute}`

Полное описание и интерактивная отладка — в Swagger UI. Для отладки также есть Postman-коллекция

OTP подтверждения — заглушка `1234`.

## Мониторинг

**Приложение и БД** (Grafana, дашборд **19004** "Spring Boot Statistics", источник Prometheus):
утилизация CPU и памяти (heap / non-heap), GC, пул соединений HikariCP.

**Инструмент нагрузки** (Grafana, дашборд **5496** "JMeter", источник InfluxDB):
RPS, времена отклика по операциям, ошибки, активные потоки — в реальном времени.

## Нагрузочное тестирование

Инструмент: Apache JMeter (`minibank-load-test.jmx`). Сценарий: регистрация + перевод, уникальные пользователи на каждый прогон.

### Тест 1 — поиск максимума

Ступенчатый рост числа потоков до точки насыщения.

| Потоки | RPS | Avg, мс | Error % | Состояние                                       |
|-------:|----:|--------:|--------:|-------------------------------------------------|
| 50  | 601  | 2   | 0% | CPU ~30%, пул свободен                          |
| 100 | 1176 | 4   | 0% | CPU ~40%, пул свободен                          |
| 200 | ~1900 | 19–31 | 0% | **пик: CPU ~92%, пул 9/10, очередь Pending 46** |
| 500 | 196  | 340 | 0% | деградация: CPU ~99%, Pending 188               |

**Вывод:** максимальная производительность ≈ **1900 RPS при ~200 одновременных пользователях**. За пиком — резкая деградация (на 500 потоках RPS падает ~10×, latency растёт ~18×). Узкое место — **CPU (2 ядра контейнера) + пул соединений HikariCP (10)**. Отказов по таймауту соединений нет (Connection Timeout = 0).

### Тест 2 — подтверждение (стабильность)

Длительная нагрузка в рабочей зоне (100 пользователей). Heap циклически очищается GC без признаков утечки, метрики восстанавливаются, ошибок и таймаутов нет. Система устойчива.
