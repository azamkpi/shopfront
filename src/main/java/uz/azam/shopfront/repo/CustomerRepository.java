package uz.azam.shopfront.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.azam.shopfront.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
