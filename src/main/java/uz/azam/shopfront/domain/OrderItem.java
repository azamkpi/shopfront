package uz.azam.shopfront.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderRequest order;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(precision = 14, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer qty = 1;
}
