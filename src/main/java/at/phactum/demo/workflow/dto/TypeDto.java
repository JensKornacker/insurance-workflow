package at.phactum.demo.workflow.dto;

import java.util.UUID;

import at.phactum.demo.insurance.persistence.InsuranceType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypeDto {
    InsuranceType insuranceType;
    UUID customerId;
    boolean mudslideRisk;
    boolean floodRisk;
    boolean sufficientIncome;
}
