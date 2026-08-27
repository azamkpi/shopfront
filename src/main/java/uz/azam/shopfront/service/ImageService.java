package uz.azam.shopfront.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import uz.azam.shopfront.config.BotProperties;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final TelegramClient telegramClient;
    private final BotProperties botProperties;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final Cache<String, byte[]> cache = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterAccess(Duration.ofHours(6))
            .build();

    public byte[] getImage(String fileId) {
        return cache.get(fileId, this::download);
    }

    private byte[] download(String fileId) {
        try {
            var file = telegramClient.execute(GetFile.builder().fileId(fileId).build());
            String url = "https://api.telegram.org/file/bot"
                    + botProperties.getToken() + "/" + file.getFilePath();

            HttpResponse<byte[]> response = httpClient.send(
                    HttpRequest.newBuilder(URI.create(url)).GET().build(),
                    HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Telegram javobi: " + response.statusCode());
            }
            return response.body();
        } catch (TelegramApiException | java.io.IOException e) {
            throw new IllegalStateException("Rasm yuklanmadi: " + fileId, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Yuklash uzildi", e);
        }
    }


}
