# Shareit
  Сервис для шеринга вещей. При нежелании покупать какой-то предмет, можно его занять у другого человека, но как найти нужный предмет и человека готового дать его? В этой поможет наш *Shareit*.
  
  
  Используется JPA, REST API, Hibernate, Spring Boot, Lombok, Maven. В будующем будет использование JUnit и Mokito для тестирования и реализовываться интеграционные тесты. Данные хронятся в базах данных: h2, posgresSQL, плогается в конце изображение базы данных в виде таблиц.
  
  #### Возможности: 
  * Пользователи: создание, обновление, удаление, получение всех и одного.
  * Вещи: создание, обновление, удаление, получение всех и одного, а так же добавление комментарий, поиск по текту.
  * Поддерживается бронирование: создание, обновление, получение по id, получение всех вещей забронированных для конкретного пользователя или для владельца.
***

  #### Будет вводится: 
   * Создание запросов на вещь, которой нет в списке вещей для бронирования.
   * Ответ от владельца на запрос.
   * Будут писаться тесты JUnit и Mockito, интеграционные тесты.

***
  #### База данных:
  
  ![Sharit](https://github.com/SuvorovaElvina/java-shareit/assets/114740144/c55d949f-544e-4c52-bec9-adfe03d2682f)
