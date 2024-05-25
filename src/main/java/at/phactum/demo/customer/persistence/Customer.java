package at.phactum.demo.customer.persistence;

import java.time.LocalDate;

import at.phactum.demo.shared.entity.Gender;
import at.phactum.demo.shared.entity.AbstractCrud;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CUSTOMER")
@Data
public class Customer extends AbstractCrud {

    @Column(name = "GENDER")
    private Gender gender;

    @Column(name = "FIRSTNAME")
    private String firstname;

    @Column(name = "LASTNAME")
    private String lastname;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "EMAIL_ADDRESS")
    private String email;
}
