package uz.azam.shopfront.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import uz.azam.shopfront.domain.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}
