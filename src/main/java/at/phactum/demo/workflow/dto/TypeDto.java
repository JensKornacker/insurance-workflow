package at.phactum.demo.workflow.dto;

import java.util.UUID;

import at.phactum.demo.insurance.entity.InsuranceType;
import lombok.Data;

@Data
public class TypeDto {
    private InsuranceType insuranceType;
    private UUID customerId;
}
