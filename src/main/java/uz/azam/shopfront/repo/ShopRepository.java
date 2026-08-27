package uz.azam.shopfront.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.azam.shopfront.domain.Shop;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findBySlug(String slug);
}
