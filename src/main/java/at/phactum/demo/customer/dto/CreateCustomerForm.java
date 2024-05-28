package at.phactum.demo.customer.dto;

import java.time.LocalDate;

import at.phactum.demo.shared.entity.Gender;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCustomerForm {
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
}
