### Shareit - приложение для шеринга

Для запуска приложения необходимо выполнить команду docker compose up

В приложении можно:

- регистрироваться, выполнять обновление и удаление своего аккаунта;
- делиться своими вещами;
- просматривать ленту с подходящими по погодным условиям вещами;
- брать вещи в аренду на какое-то время, при этом её хозяин может подтверждать или отклонять бронирование;
- оставлять запросы на нужную вещь, между тем как другие пользователи могут просматривать их и выкладывать вещи под эти запросы;
- оставлять комментарии к вещи, но только в случае, если пользователь, действительно, ею пользовался;
- искать вещи с помощью ключевых слов;

В приложении предусмотрено две роли:

- Пользователь
- Модератор

Модератор дополнительно имеет доступ к страницам, которые находится по пути:
- /users, где он может просматривать список всех пользователей, а также удалять их аккаунты;
- /users/create, где может создавать пользователей;

Первый модератор создается путем регистрации пользователя с логином moderator.

Приложение запускается на порту 8183.
