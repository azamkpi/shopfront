package uz.azam.shopfront.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaGroupBuffer {

    private static final long WAIT_MILLIS = 1500;

    private final Map<String, Batch> batches = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Getter
    public static class Batch {
        private final List<PhotoRef> photos = new ArrayList<>();
        private String caption;
        private Long chatId;
        private ScheduledFuture<?> task;
    }

    public record PhotoRef(String fileId, String fileUniqueId, String thumbFileId) {}

    public void add(String groupKey, Long chatId, PhotoRef photo,
                    String caption, Consumer<Batch> onComplete) {

        Batch batch = batches.computeIfAbsent(groupKey, k -> new Batch());

        synchronized (batch) {
            batch.chatId = chatId;
            batch.photos.add(photo);
            if (caption != null && !caption.isBlank()) {
                batch.caption = caption;
            }

            if (batch.task != null) {
                batch.task.cancel(false);
            }

            batch.task = scheduler.schedule(() -> {
                batches.remove(groupKey);
                try {
                    onComplete.accept(batch);
                } catch (Exception e) {
                    log.error("Batch ishlashda xato: {}", groupKey, e);
                }
            }, WAIT_MILLIS, TimeUnit.MILLISECONDS);
        }
    }


}
