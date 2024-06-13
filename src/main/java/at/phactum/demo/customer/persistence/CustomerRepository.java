package at.phactum.demo.customer.persistence;

import java.util.Set;
import java.util.UUID;

import at.phactum.demo.insurance.persistence.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query(value = "select i.* from insurance i, customer c, customer_insurance ci where c.id = ci.customer_id and ci.insurance_id = i.id and c.id = :customerId",
            nativeQuery = true)
    Set<Insurance> getInsurancesForCustomer(UUID customerId);
}
