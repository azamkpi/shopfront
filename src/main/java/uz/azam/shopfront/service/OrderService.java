package uz.azam.shopfront.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.azam.shopfront.domain.OrderItem;
import uz.azam.shopfront.domain.OrderRequest;
import uz.azam.shopfront.domain.ProductVariant;
import uz.azam.shopfront.repo.OrderRequestRepository;
import uz.azam.shopfront.repo.ProductVariantRepository;
import uz.azam.shopfront.util.MoneyUtils;
import uz.azam.shopfront.util.PhoneUtils;
import uz.azam.shopfront.web.dto.OrderCreateDto;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRequestRepository orderRepository;
    private final ProductVariantRepository variantRepository;
    private final ShopContext shopContext;
    private final NotificationService notificationService;
    private final CustomerService customerService;

    @Transactional
    public OrderRequest create(OrderCreateDto dto, Long tgUserId) {
        if (dto.phone() == null || dto.phone().isBlank()) {
            throw new IllegalArgumentException("Telefon raqam kerak");
        }

        if (dto.items() == null || dto.items().isEmpty()) {
            throw new IllegalArgumentException("Savat bo'sh");
        }

        OrderRequest order = new OrderRequest();
        order.setShopId(shopContext.shopId());
        order.setVariantId(dto.variantId());
        order.setTgUserId(tgUserId);
        order.setFullName(dto.fullName());
        order.setNote(dto.note());

        String phone = PhoneUtils.normalize(dto.phone());
        if (phone == null) {
            throw new IllegalArgumentException("Telefon raqam noto'g'ri");
        }
        order.setPhone(phone);

        BigDecimal total = BigDecimal.ZERO;

        for (var line : dto.items()) {
            int qty = (line.qty() == null || line.qty() < 1) ? 1 : Math.min(line.qty(), 99);

            ProductVariant v = variantRepository.findById(line.variantId())
                    .orElseThrow(() -> new IllegalArgumentException("Mahsulot topilmadi"));

            OrderItem item = new OrderItem();
            item.setVariantId(v.getId());
            item.setProductName(v.getProduct().getName());
            item.setPrice(v.getPrice());
            item.setQty(qty);
            order.addItem(item);

            if (v.getPrice() != null) {
                total=total.add(v.getPrice().multiply(BigDecimal.valueOf(qty)));
            }
        }

        order.setTotal(total);

        OrderRequest saved = orderRepository.save(order);

        customerService.save(tgUserId, saved.getPhone(), saved.getFullName());

        notifyAdmin(saved);

        if (order.getTgUserId() != null) {
            notificationService.send(order.getTgUserId(),
                    "✅ Buyurtmangiz qabul qilindi (#" + order.getId() + ").\n"
                            + "Tez orada siz bilan bog'lanamiz.");
        }

        return saved;
    }

    private void notifyAdmin(OrderRequest order) {
        StringBuilder sb = new StringBuilder();
        sb.append("🔔 Yangi buyurtma #").append(order.getId()).append("\n\n");

        for (OrderItem it : order.getItems()) {
            sb.append("• ").append(it.getProductName())
                    .append(" × ").append(it.getQty());
            if (it.getPrice() != null) {
                sb.append(" — ").append(MoneyUtils.format(it.getPrice()
                        .multiply(BigDecimal.valueOf(it.getQty()))));
            } else {
                sb.append(" — narx kelishiladi");
            }
            sb.append("\n");
        }

        if (order.getTotal() != null && order.getTotal().signum() > 0) {
            sb.append("\n💰 Jami: ").append(MoneyUtils.format(order.getTotal())).append("\n");
        }

        sb.append("\n👤 ").append(order.getFullName() != null ? order.getFullName() : "—")
                .append("\n📞 ").append(PhoneUtils.format(order.getPhone()));

        if (order.getNote() != null && !order.getNote().isBlank()) {
            sb.append("\n💬 ").append(order.getNote());
        }

        notificationService.send(shopContext.shop().getAdminChatId(), sb.toString());
    }
}
