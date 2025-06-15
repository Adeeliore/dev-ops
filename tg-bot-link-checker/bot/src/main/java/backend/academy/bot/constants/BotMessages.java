package backend.academy.bot.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotMessages {
    public static final String INVALID_LINK = "Ошибка: Введена некорректная ссылка.";
    public static final String LINK_ALREADY_TRACKED =
            "Ошибка: Ссылка уже отслеживается.\nВведите новую ссылку или отправьте 'назад' для возврата.";
    public static final String ENTER_TAGS = "Введите теги (через пробел) или 'пропустить':";
    public static final String ENTER_FILTERS =
            "Введите фильтры (через пробел, например: PR Issue Answer Comment user=имя_пользователя) или 'пропустить':";
    public static final String ENTER_LINK = "Введите ссылку для отслеживания:";
    public static final String TRACKING_ERROR = "Ошибка: Ссылка уже отслеживается.";
    public static final String BACK_TO_LINK = "Введите ссылку еще раз:";
    public static final String BACK_TO_TAGS = "Введите теги еще раз (или 'пропустить'):";
    public static final String BACK_TO_FILTERS = "Введите фильтры еще раз (или 'пропустить'):";
    public static final String BACK_TO_MENU = "Вы вернулись в главное меню. Введите команду.";
    public static final String UNKNOWN_COMMAND = "Неизвестная команда. Введите /help для списка команд.";
    public static final String INVALID_FILTER =
            "Ошибка: Некорректный фильтр '%s'. Допустимы: PR, Issue, Answer, Comment, user=<username>";

    public static final String LINK_TRACKED_TEMPLATE = "Ссылка %s отслеживается с тегами %s и фильтрами %s";

    public static final String START_MESSAGE = "Привет! Я бот для отслеживания ссылок, добро пожаловать!";
    public static final String HELP_MESSAGE =
            """
    Доступные команды:
    /start - регистрация
    /help - список команд
    /track - начать отслеживание
    /untrack <url> - прекратить отслеживание
    /list - показать список отслеживаемых ссылок
    """;
    public static final String REGISTRATION_ERROR = "Ошибка регистрации в Scrapper: ";
    public static final String UNTRACK_NO_LINK = "Ошибка: укажите ссылку для удаления.";
    public static final String LINK_REMOVED = "Ссылка удалена.";
    public static final String LINK_NOT_FOUND = "Ссылка не найдена.";
    public static final String NO_TRACKED_LINKS = "У вас пока нет отслеживаемых ссылок.";
    public static final String TRACKED_LINKS_PREFIX = "Ваши отслеживаемые ссылки:\n";
}
