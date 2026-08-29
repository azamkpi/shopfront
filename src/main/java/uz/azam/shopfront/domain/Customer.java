package uz.azam.shopfront.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@RequiredArgsConstructor
@Getter
@Setter
public class Customer {

    @Id
    @Column(name = "tg_user_id")
    private Long tgUserId;

    private String phone;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
