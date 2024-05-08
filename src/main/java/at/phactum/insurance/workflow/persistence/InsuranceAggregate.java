package at.phactum.insurance.workflow.persistence;

import java.util.UUID;

import at.phactum.insurance.domain.AbstractCrud;
import at.phactum.insurance.domain.InsuranceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "INSURANCE_AGGREGATE")
@Data
public class InsuranceAggregate extends AbstractCrud {

    @Column(name = "credit_check_outcome")
    private String creditCheckOutcome;

    @Column(name = "risk_assessment_outcome")
    private String riskAssessmentOutcome;

    @Column(name = "manual_risk_assessment_outcome")
    private String manualRiskAssessmentOutcome;

    @Column(name = "manual_credit_check_outcome")
    private String manualCreditCheckOutcome;

}
