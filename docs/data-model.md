# Модели данных

Документ описывает модели данных исходного Java-проекта «Online schedule»
(учебное расписание с оценками) и их отображение на реляционную схему в Kotlin-версии.

## 1. Модели исходного проекта (Java)

Исходный проект не использовал БД: данные загружались из текстовых/XML-файлов
(`un_psw.txt`, `un_ns.txt`, `group_list.txt`, `subjects_data.xml`) в память
(singleton `myDataBase`) и сохранялись обратно на диск при остановке приложения.

### 1.1 Пользователи (иерархия Worker)

```
WorkerInterface
      ▲
   Worker (abstract)     un, name, surname
      ├── Student        + group: int
      ├── Teacher        + subjects: List<String>
      └── Admin          (без дополнительных полей)
```

Роль пользователя определялась префиксом логина (`un`):

| Префикс | Роль    | Пример |
|---------|---------|--------|
| `st`    | Student | `st1`  |
| `te`    | Teacher | `te1`  |
| `ad`    | Admin   | `ad1`  |

Пароли хранились отдельно, в `HashMap<String, String>` (`un → password`), в открытом виде.

### 1.2 Группы

`Group`:

| Поле      | Тип                  | Описание           |
|-----------|----------------------|--------------------|
| `groupId` | `int`                | номер группы       |
| `list`    | `ArrayList<Student>` | студенты группы    |

### 1.3 Предметы и занятия

Центральная сущность — `dataNode`: назначение «предмет + преподаватель + группа»
со списком занятий.

`dataNode`:

| Поле          | Тип                 | Описание                     |
|---------------|---------------------|------------------------------|
| `subjectName` | `String`            | название предмета            |
| `teacherId`   | `String`            | `un` преподавателя           |
| `groupId`     | `int`               | номер группы                 |
| `lessons`     | `ArrayList<lesson>` | занятия по этому назначению  |

`lesson`:

| Поле      | Тип                            | Описание                        |
|-----------|--------------------------------|---------------------------------|
| `name`    | `String`                       | название занятия                |
| `date`    | `LocalDate`                    | дата занятия                    |
| `results` | `HashMap<String, lessonNode>`  | `un` студента → оценка/комментарий |

`lesson.lessonNode` (внутренний класс):

| Поле      | Тип      | Описание                                  |
|-----------|----------|-------------------------------------------|
| `mark`    | `int`    | оценка (0 — не выставлена, иначе 2–5)     |
| `comment` | `String` | комментарий преподавателя (может быть null) |

### 1.4 Логические связи

```
Teacher 1 ──── N dataNode (subjectName, groupId)
Group   1 ──── N dataNode
Group   1 ──── N Student
dataNode 1 ─── N lesson
lesson  1 ──── N lessonNode (по одному на студента группы)
lessonNode N ─ 1 Student (ключ map — un студента)
```

`Teacher.subjects` — денормализованный список названий предметов,
вычислявшийся из `dataNode` при загрузке.

## 2. Реляционная схема Kotlin-версии (JPA / Hibernate)

Та же доменная модель, но в нормализованном виде в PostgreSQL (H2 в тестах).
Иерархия `Worker` отображена стратегией SINGLE_TABLE с дискриминатором `role`.

```
┌────────────────┐      ┌───────────────┐
│    worker      │      │  study_group  │
│────────────────│      │───────────────│
│ un (PK)        │  N:1 │ id (PK)       │
│ role (STUDENT/ │─────▶│               │
│  TEACHER/ADMIN)│      └───────┬───────┘
│ name           │              │ 1:N
│ surname        │      ┌───────▼─────────────┐
│ password       │      │ subject_assignment  │
│ group_id (FK,  │      │─────────────────────│
│  только Student│      │ id (PK)             │
└───────┬────────┘      │ subject_name        │
        │ 1:N           │ teacher_un (FK)     │
        │               │ group_id  (FK)      │
        │               └───────┬─────────────┘
        │                       │ 1:N
        │               ┌───────▼───────┐
        │               │    lesson     │
        │               │───────────────│
        │               │ id (PK)       │
        │               │ name          │
        │               │ date          │
        │               │ assignment_id │
        │               └───────┬───────┘
        │ 1:N                   │ 1:N
        │               ┌───────▼───────┐
        └──────────────▶│ lesson_result │
                        │───────────────│
                        │ id (PK)       │
                        │ lesson_id (FK)│
                        │ student_un(FK)│
                        │ mark          │
                        │ comment       │
                        └───────────────┘
```

### Соответствие моделей

| Java (in-memory)             | Kotlin (JPA entity)   | Таблица              |
|------------------------------|-----------------------|----------------------|
| `Worker` / `Student` / `Teacher` / `Admin` | `Worker` (SINGLE_TABLE, `@DiscriminatorColumn role`) | `worker` |
| `pwdBase` (map un→password)  | поле `Worker.password`| `worker`             |
| `Group`                      | `StudyGroup`          | `study_group`        |
| `dataNode`                   | `SubjectAssignment`   | `subject_assignment` |
| `lesson`                     | `Lesson`              | `lesson`             |
| `lesson.lessonNode` + ключ map | `LessonResult`      | `lesson_result`      |
| `Teacher.subjects` (денормализация) | вычисляется запросом к `subject_assignment` | — |

Первичный ключ пользователя — прежний логин `un` (`st1`, `te2`, `ad1`),
поэтому семантика «роль по префиксу» сохранена.

Исходные данные из файлов исходного проекта переносятся при первом старте
сидером (`DataSeeder`) — состав пользователей, групп, предметов, занятий и оценок
идентичен исходным `un_psw.txt` / `un_ns.txt` / `group_list.txt` / `subjects_data.xml`.
