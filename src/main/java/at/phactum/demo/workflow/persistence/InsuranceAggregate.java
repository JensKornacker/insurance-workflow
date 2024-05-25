package at.phactum.demo.workflow.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;

@Entity
@Table(name = "INSURANCE_AGGREGATE")
@Data
public class InsuranceAggregate {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private String id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "credit_check_outcome")
    private String creditCheckOutcome;

    @Column(name = "risk_assessment_outcome")
    private String riskAssessmentOutcome;

    @Column(name = "manual_risk_assessment_outcome")
    private String manualRiskAssessmentOutcome;

    @Column(name = "manual_credit_check_outcome")
    private String manualCreditCheckOutcome;

    @Column(name = "insurance_type")
    private String insuranceType;

    @Column(name = "customer")
    private UUID customer;

}
