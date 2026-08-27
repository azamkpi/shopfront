package uz.azam.shopfront.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import uz.azam.shopfront.domain.Shop;
import uz.azam.shopfront.repo.ShopRepository;
import uz.azam.shopfront.service.CaptionParser;
import uz.azam.shopfront.service.MediaGroupBuffer;
import uz.azam.shopfront.service.ProductService;
import uz.azam.shopfront.service.ShopContext;

import java.math.BigDecimal;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateHandler {

    private final TelegramClient telegramClient;
    private final ShopContext shopContext;
    private final ShopRepository shopRepository;
    private final MediaGroupBuffer mediaGroupBuffer;
    private final CaptionParser captionParser;
    private final ProductService productService;

    public void handle(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        Message message = update.getMessage();

        if (message.hasPhoto()) {
            handlePhoto(message);
            return;
        }

        if (message.hasText()) {
            handleText(message);
        }
    }

    private void handlePhoto(Message message) {
        Long chatId = message.getChatId();

        if (!isAdmin(chatId)) {
            send(chatId, "Faqat admin mahsulot qo'sha oladi.");
            return;
        }

        // eng katta o'lchamdagi rasmni olamiz
        PhotoSize photo = message.getPhoto().stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow();

        var ref = new MediaGroupBuffer.PhotoRef(photo.getFileId(), photo.getFileUniqueId());
        String groupKey = message.getMediaGroupId() != null
                ? message.getMediaGroupId()
                : "single-" + message.getMessageId();

        mediaGroupBuffer.add(groupKey, chatId, ref, message.getCaption(),
                batch -> saveProduct(batch));
    }

    private void saveProduct(MediaGroupBuffer.Batch batch) {
        Long chatId = batch.getChatId();

        if (batch.getCaption() == null) {
            send(chatId, "❗️ Rasmga izoh yozmadingiz.\n\nFormat:\nNomi\nNarxi\n#kategoriya\nTavsif");
            return;
        }

        try {
            var parsed = captionParser.parse(batch.getCaption());
            var product = productService.create(parsed, batch.getPhotos());

            String priceText = parsed.negotiable()
                    ? "Kelishiladi"
                    : (parsed.price() != null ? formatPrice(parsed.price()) : "Ko'rsatilmagan");

            send(chatId, """
                ✅ Qo'shildi
                
                📦 %s
                💰 %s
                🖼 %d ta rasm
                🆔 %d
                """.formatted(product.getName(), priceText,
                    batch.getPhotos().size(), product.getId()));

        } catch (Exception e) {
            log.error("Mahsulot saqlanmadi", e);
            send(chatId, "❌ Xato: " + e.getMessage());
        }
    }

    private boolean isAdmin(Long chatId) {
        Long adminId = shopContext.shop().getAdminChatId();
        return adminId != null && adminId.equals(chatId);
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%,d so'm", price.longValue()).replace(',', ' ');
    }

    private void handleText(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText().trim();

        log.debug("Xabar: chatId={}, text={}", chatId, text);

        if (text.startsWith("/start")) {
            handleStart(chatId);
        } else if (text.equals("/admin")) {
            handleAdminClaim(chatId);
        } else if (text.equals("/help")) {
            send(chatId, """
                📝 Mahsulot qo'shish
                
                Rasm(lar)ni tashlang va izohga yozing:
                
                Dior Sauvage EDP 100ml
                650000
                #erkaklar
                Original Fransiya, uzoq hidi bor
                
                Qoidalar:
                • 1-qator — mahsulot nomi
                • Raqamli qator — narx
                • "kelishiladi" — narx kelishiladi
                • #teg — kategoriya
                • Qolgani — tavsif
                """);
        } else {
            send(chatId, "Buyruqni tushunmadim, /helpni bosing.");
        }
    }

    private void handleStart(Long chatId) {
        Shop shop = shopContext.shop();
        String welcome = shop.getWelcomeText() != null
                ? shop.getWelcomeText()
                : "Xush kelibsiz!";

        send(chatId, "🛍 " + shop.getTitle() + "\n\n" + welcome
                + "\n\nKatalog tez orada shu yerda ochiladi.");
    }

    private void handleAdminClaim(Long chatId) {
        Shop shop = shopContext.shop();

        if (shop.getAdminChatId() == null) {
            shop.setAdminChatId(chatId);
            shopRepository.save(shop);
            send(chatId, "✅ Siz admin sifatida ro'yxatdan o'tdingiz.\nChat ID: " + chatId);
            log.info("Admin belgilandi: chatId={}", chatId);
        } else if (shop.getAdminChatId().equals(chatId)) {
            send(chatId, "Siz allaqachon adminsiz.");
        } else {
            send(chatId, "Bu do'konning admini allaqachon belgilangan.");
        }

        shopContext.refresh();
    }

    private void send(Long chatId, String text) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Xabar yuborilmadi: chatId={}", chatId, e);
        }
    }

}
