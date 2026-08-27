package uz.azam.shopfront.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.azam.shopfront.domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByShopIdOrderBySortOrderAsc(Long shopId);

    Optional<Category> findByShopIdAndSlug(Long shopId, String slug);
}
