package at.phactum.insurance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

}
