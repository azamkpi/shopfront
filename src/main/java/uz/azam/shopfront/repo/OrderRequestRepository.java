package uz.azam.shopfront.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.azam.shopfront.domain.OrderRequest;

public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {
}
