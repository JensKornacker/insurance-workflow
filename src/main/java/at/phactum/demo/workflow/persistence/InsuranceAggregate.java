package at.phactum.demo.workflow.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "mudslide_risk")
    private boolean mudslideRisk = false;

    @Column(name = "flood_risk")
    private boolean floodRisk = false;

    @Column(name = "sufficient_income")
    private boolean sufficientIncome = false;

    @Column(name = "further_information")
    private String furtherInformation;

    @Column(name = "monthly_income")
    private Integer monthlyIncome;

    @Column(name = "insurance_coverage")
    String insuranceCoverage;

    @Column(name = "insurance_sum")
    private String insuranceSum;

    @Column(name = "liability_check")
    private String liabilityCheck;

    @Column(name = "payment_schedule")
    private String paymentSchedule;

    @Column(name = "amount")
    private Integer amount;

}
