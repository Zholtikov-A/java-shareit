# Share iT
Платформа для шеринга вещей: позволяет пользователям рассказывать,
какими вещами они готовы поделиться,
а также находить нужную вещь и брать её в аренду на какое-то время.
<div>
<img width="1012" alt="DataBase schema" src="assets/ShareItLanding.png">
</div>
Микросервисное приложение, состоящее из основного сервиса и шлюза.

## Основная функциональность
* Регистрация, обновление и получение данных пользователей.
* Добавление, обновление, получение, а также поиск по вещам в аренду.
* Управление заявками на бронирование вещей, найденных в системе.
* Обработка запросов на аренду желаемых вещей, не найденных в системе.
  Если вещь добавляется в систему в ответ на подобный запрос, к данным вещи привязывается id запроса.
* Комментирование успешно завершённой аренды.

## Инструкция по развёртыванию ▶️
1) Склонируйте репозиторий: https://github.com/Zholtikov-A/java-shareit.git
2) Откройте программу Docker
3) В терминале или командной строке перейдите в папку проекта (где лежит файл docker-compose.yml) и выполните команду: docker-compose up
4) В программе Docker должны появиться 3 контейнера
5) Программа доступна по ниже описанному API по адресу: http://localhost:8080

## API
* POST /users - добавление пользователя
* PATCH /users/{id} - обновление данных пользователя
* GET /users/{id} - получение данных пользователя
* GET /users/ - получение списка всех пользователей
* DELETE /users/{id} - удаление пользователя по id

* POST /items - добавление вещи
* PATCH /items/{itemId} - обновление данных вещи
* GET /items/{itemId} - получение данных вещи
* GET /items/ - получение списка вещей
* GET /items/search - поиск вещей по тексту в параметре text
* POST /items/{itemId}/comment - добавление отзыва к вещи после завершенного бронирования

* POST /requests - добавление запроса на аренду
* GET /requests - получение списка запросов аренды для пользователя по id пользователя (передается в заголовке)
* GET /requests/all - получение списка запросов аренды иных пользователей (кроме id пользователя, переданного в заголовке)
* GET /requests/{requestId} - получение запроса аренды

* GET /bookings/ - получение бронирований по фильтрам state, from, size
* GET /bookings/owner - получение бронирований пользователя по фильтрам state, from, size
* POST /bookings/ - создание бронирования
* PATCH /bookings/{bookingId} - одобрение или отклонение бронирования по параметру approved
* GET /bookings/{bookingId} - получение данных о бронировании

## Testing

* Postman tests collection: postman/ShareItPostmanTests.json
* JUnit tests: server/src/test/java

## 🛠 Tech & Tools

<div>
      <img src="https://github.com/Salaia/icons/blob/main/green/Java.png?raw=true" title="Java" alt="Java" height="40"/>
      <img src="https://github.com/Salaia/icons/blob/main/green/SPRING%20boot.png?raw=true" title="Spring Boot" alt="Spring Boot" height="40"/>
      <img src="https://github.com/Salaia/icons/blob/main/green/SPRING%20MVC.png?raw=true" title="Spring MVC" alt="Spring MVC" height="40"/>
      <img src="https://github.com/Salaia/icons/blob/main/green/Maven.png?raw=true" title="Apache Maven" alt="Apache Maven" height="40"/>
<img src="https://github.com/Salaia/icons/blob/main/green/Rest%20API.png?raw=true" title="Rest API" alt="Rest API" height="40"/>
      <img src="https://github.com/Salaia/icons/blob/main/green/Microservice.png?raw=true" title="Microservice" alt="Microservice" height="40"/>
<img src="https://github.com/Salaia/icons/blob/main/green/H2.png?raw=true" title="H2" alt="H2" height="40"/>
<img src="https://github.com/Salaia/icons/blob/main/green/PostgreSQL.png?raw=true" alt="PostgreSQL" height="40"/> 
<img src="https://github.com/Salaia/icons/blob/main/green/Hibernate.png?raw=true" title="Hibernate" alt="Hibernate" height="40"/>
      <img src="https://github.com/Salaia/icons/blob/main/green/Lombok.png?raw=true" title="Lombok" alt="Lombok" height="40"/>
      <img src="https://github.com/Salaia/icons/blob/main/green/Mockito.png?raw=true" title="Mockito" alt="Mockito" height="40"/>
<img src="https://github.com/Salaia/icons/blob/main/green/JUnit%205.png?raw=true" title="JUnit 5" alt="JUnit 5" height="40"/>
<img src="https://github.com/Salaia/icons/blob/main/green/Postman.png?raw=true" title="Postman" alt="Postman" height="40"/>
<img src="https://github.com/Salaia/icons/blob/main/green/Docker.png?raw=true" title="Docker" alt="Docker" height="40"/>
</div>

## Статус и планы по доработке проекта

На данный момент проект проверен и зачтен ревьюером. Планов по дальнейшему развитию проекта нет.

