package at.phactum.demo.customer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import at.phactum.demo.insurance.persistence.Insurance;
import at.phactum.demo.shared.entity.Gender;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDto {
    UUID id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Gender gender;
    String firstname;
    String lastname;
    LocalDate dateOfBirth;
    String phoneNumber;
    String email;
    String street;
    String zipCode;
    String city;
    String country;
    int customerNumber;
    Set<Insurance> insurances;

}
