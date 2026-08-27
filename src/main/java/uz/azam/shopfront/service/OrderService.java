package uz.azam.shopfront.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.azam.shopfront.domain.OrderRequest;
import uz.azam.shopfront.repo.OrderRequestRepository;
import uz.azam.shopfront.web.dto.OrderCreateDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRequestRepository orderRepository;
    private final ShopContext shopContext;

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

        return orderRepository.save(order);
    }
}
