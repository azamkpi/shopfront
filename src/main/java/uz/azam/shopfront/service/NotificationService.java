package uz.azam.shopfront.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final TelegramClient telegramClient;

    public void send(Long chatId, String text) {
        if (chatId == null) {
            log.warn("chatId null, xabar yuborilmadi");
            return;
        }
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error("Xabar yuborilmadi: chatId={}", chatId, e);
        }
    }
}
