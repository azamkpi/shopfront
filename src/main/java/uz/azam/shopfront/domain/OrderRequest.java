package uz.azam.shopfront.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_request")
@Getter
@Setter
public class OrderRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "tg_user_id")
    private Long tgUserId;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    @Column(columnDefinition = "text")
    private String note;

    @Column(nullable = false)
    private String status = "NEW";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
