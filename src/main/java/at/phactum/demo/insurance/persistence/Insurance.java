package at.phactum.demo.insurance.persistence;

import java.util.Set;

import at.phactum.demo.customer.persistence.Customer;
import at.phactum.demo.shared.entity.AbstractCrud;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "INSURANCE")
@Data
public class Insurance extends AbstractCrud {

    @Column(name = "NAME")
    private String name;

    @Column(name = "SIMPLE_NAME")
    private String simpleName;

    @Column(name = "INSURANCE_TYPE")
    private InsuranceType insuranceType;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "insurances")
    private Set<Customer> customers;

    @Column(name = "insurance_number")
    private Integer insuranceNumber;

}
