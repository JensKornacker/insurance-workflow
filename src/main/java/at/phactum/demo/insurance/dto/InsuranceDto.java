package at.phactum.demo.insurance.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import at.phactum.demo.customer.persistence.Customer;
import at.phactum.demo.insurance.persistence.InsuranceType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InsuranceDto {
    UUID id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String name;
    String simpleName;
    InsuranceType insuranceType;
    Set<Customer> customers;
}
