Для логина при помощи дополнительного атрибута:
1. При запуске keycloak указыываем наименование атрибута в переменной 'KC_SPI_AUTHENTICATOR_ADD_NAME_USERNAME'. Если переменная не указана будет использоваться аттрибут phone_number:
2. В Админ панели переходим по вкладке 'Authentication'
3. Выбираем 'browser' из списка.  
4. Дублируем флоу нажав 'duplicate' в списке 'action' в правом верхнем углу
5. В дублированном флоу удаляем шаг 'Username Password Form'.
6. Нажимаем на кнопку 'Add Step' и добавляем новый шаг 'Attribute Username Password Authenticator' из списка.
7. Нажимаем на вкладку 'Authentication' и справа от нового флоу нажимаем на кнопку 'bind flow'.
8. Из списка флоу выбираем 'Browser Flow' и сохраняем.

Для включения функции регистрации:
1.	Пройти по вкладке 'Realm settings'
2.  Включить функцию  'User Profile Enabled' во вкладке 'General'

Для добавления нового поля на странице регистрации пользователя через админ панель:
1.	Запустить приложение с опцией '--features=declarative-user-profile'
2.	Пройти по вкладке 'Realm settings'
3.  Включить функцию 'User Profile Enabled' во вкладке 'General'. Появляется новая вкладка 'User Profile'
4.  Во вкладке 'User Profile' нажимаем на кнопку 'Create attribute' и создаем кастомное поле. Тут можно создать полe 'phone_number'.

После добавления атрибута phone_number можно добавить к нему кастомную проверку для уникальности:
1. Пройти по вкладке 'Realm settings' -> 'User Profile' -> 'JSON editor'
2. Добавить текст указанный ниже в созданный атрибут 'phone_number' в поле 'validations'
"unique-attribute": {
"attribute_name": "phone_number"
}

Для отправки данных в другой микросервис при регистрации пользователя:
1. Пройти по вкладке 'Realm settings', затем по вкладке 'Events'
2. В списке 'Event Listeners' выбрать 'user-registered'. Teперь при регистрации пользователя будет выполняться код из класса kz.air.keycloak.spi.listener.UserRegisteredListenerProvider. В коде provider слушает событие при регистрации пользователя и отправляет HTTP запрос на адрес указанный переменной 'KC_SPI_EVENTS_LISTENER_USER_REGISTERED_EXTERNAL_SERVICE'. Для редактирования запроса нужно редактировать метод kz.air.keycloak.spi.transaction.RegisteredUserSyncTransaction

Kастомный REST endpoint:
1. http://{host}/realms/{realm}/{resource-provider-id}/{endpoint}


