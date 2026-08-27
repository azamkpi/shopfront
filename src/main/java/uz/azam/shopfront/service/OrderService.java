package uz.azam.shopfront.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.azam.shopfront.domain.OrderRequest;
import uz.azam.shopfront.repo.OrderRequestRepository;
import uz.azam.shopfront.repo.ProductVariantRepository;
import uz.azam.shopfront.web.dto.OrderCreateDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRequestRepository orderRepository;
    private final ProductVariantRepository variantRepository;
    private final ShopContext shopContext;
    private final NotificationService notificationService;

    @Transactional
    public OrderRequest create(OrderCreateDto dto, Long tgUserId) {
        if (dto.phone() == null || dto.phone().isBlank()) {
            throw new IllegalArgumentException("Telefon raqam kerak");
        }

        OrderRequest order = new OrderRequest();
        order.setShopId(shopContext.shopId());
        order.setVariantId(dto.variantId());
        order.setTgUserId(tgUserId);
        order.setFullName(dto.fullName());
        order.setPhone(dto.phone().trim());
        order.setNote(dto.note());

        OrderRequest saved = orderRepository.save(order);
        notifyAdmin(saved);

        if (order.getTgUserId() != null) {
            notificationService.send(order.getTgUserId(),
                    "✅ Buyurtmangiz qabul qilindi (#" + order.getId() + ").\n"
                            + "Tez orada siz bilan bog'lanamiz.");
        }

        return saved;
    }

    private void notifyAdmin(OrderRequest order) {
        String productName = "Noma'lum";
        if (order.getVariantId() != null) {
            productName = variantRepository.findById(order.getVariantId())
                    .map(v -> v.getProduct().getName())
                    .orElse("Noma'lum");
        }

        String text = """
                🔔 Yangi buyurtma #%d
                
                📦 %s
                👤 %s
                📞 %s
                %s
                """.formatted(
                order.getId(),
                productName,
                order.getFullName() != null ? order.getFullName() : "—",
                order.getPhone(),
                order.getNote() != null && !order.getNote().isBlank()
                        ? "💬 " + order.getNote() : ""
        );

        notificationService.send(shopContext.shop().getAdminChatId(), text);
    }
}
