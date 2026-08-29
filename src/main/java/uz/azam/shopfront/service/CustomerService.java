package uz.azam.shopfront.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.azam.shopfront.domain.Customer;
import uz.azam.shopfront.repo.CustomerRepository;
import uz.azam.shopfront.util.PhoneUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public void save(Long tgUserId, String phone, String fullName) {
        if (tgUserId == null) return;

        Customer c = customerRepository.findById(tgUserId).orElseGet(() -> {
            Customer n = new Customer();
            n.setTgUserId(tgUserId);
            return n;
        });

        if (phone != null && !phone.isBlank()) {
            c.setPhone(PhoneUtils.normalize(phone));
        }
        if (fullName != null && !fullName.isBlank()) {
            c.setFullName(fullName);
        }
        c.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(c);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> find(Long tgUserId) {
        return tgUserId == null ? Optional.empty() : customerRepository.findById(tgUserId);
    }
}
