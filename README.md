# Online Schedule

Учебное веб-приложение «расписание и оценки» с тремя ролями: студент,
преподаватель, администратор. Kotlin-порт исходного Java/Servlet-проекта
с сохранением архитектуры и сценариев, но на современном стеке:

- **Kotlin + Spring Boot 3** (MVC, Thymeleaf)
- **Hibernate / Spring Data JPA** (ORM) + **PostgreSQL**
- **Redis** — кеш расписаний
- **Gradle** — сборка и тесты (JUnit 5 + H2)
- **Docker / docker-compose** — сборка и локальный запуск

Описание моделей данных и логики работы — в [`docs/`](docs):
[data-model.md](docs/data-model.md), [architecture.md](docs/architecture.md).

## Быстрый старт (нужен только Docker)

```bash
docker compose up --build
```

При сборке образа прогоняются тесты; затем поднимаются PostgreSQL, Redis
и приложение на <http://localhost:8080>. При первом старте БД заполняется
демо-данными.

Демо-аккаунты (логин / пароль):

| Роль          | Логин | Пароль |
|---------------|-------|--------|
| Студент       | `st1` | `123`  |
| Преподаватель | `te1` | `123`  |
| Администратор | `ad1` | `123`  |

## Сборка и тесты без Docker (нужен JDK 21)

```bash
./gradlew test      # тесты (H2 in-memory, БД не нужна)
./gradlew bootJar   # сборка jar
```

Запуск локально собранного jar требует PostgreSQL и Redis (можно поднять
только их: `docker compose up db redis`), адреса задаются переменными
`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`,
`SPRING_DATASOURCE_PASSWORD`, `SPRING_DATA_REDIS_HOST`.

Прогнать тесты без JDK на хосте можно и через Docker:

```bash
docker build --target build .
```

## Структура проекта

```
src/main/kotlin/com/course/schedule/
├── domain/       # JPA-сущности: Worker(Student/Teacher/Admin), StudyGroup,
│                 # SubjectAssignment, Lesson, LessonResult
├── repository/   # Spring Data JPA репозитории
├── service/      # AuthService, ScheduleService (кеш Redis), AdminService
├── config/       # кеш, интерцептор логина, сидер демо-данных
└── web/          # контроллеры: Auth, Student, Teacher, Admin
src/main/resources/
├── templates/    # Thymeleaf-шаблоны (порт JSP/HTML исходного проекта)
└── static/       # CSS и JS исходного проекта
src/test/kotlin/  # JUnit 5 тесты сервисного слоя (H2)
```
