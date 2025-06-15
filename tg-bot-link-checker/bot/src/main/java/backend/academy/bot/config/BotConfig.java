package backend.academy.bot.config;

import backend.academy.bot.AppConfig;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Bean
    public TelegramBot telegramBot(AppConfig appConfig) {
        return new TelegramBot(appConfig.telegramToken());
    }
}
