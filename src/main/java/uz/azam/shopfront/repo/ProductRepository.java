package uz.azam.shopfront.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.azam.shopfront.domain.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByShopIdAndActiveTrueOrderByCreatedAtDesc(Long shopId, Pageable pageable);

    Page<Product> findByShopIdAndCategoryIdAndActiveTrueOrderByCreatedAtDesc(
            Long shopId, Long categoryId, Pageable pageable
    );

    Optional<Product> findByIdAndShopId(Long id, Long shopId);
}
