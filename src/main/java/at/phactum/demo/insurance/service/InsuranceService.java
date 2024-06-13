package at.phactum.demo.insurance.service;

import java.time.LocalDateTime;
import java.util.UUID;

import at.phactum.demo.customer.persistence.Customer;
import at.phactum.demo.customer.persistence.CustomerRepository;
import at.phactum.demo.insurance.persistence.Insurance;
import at.phactum.demo.insurance.persistence.InsuranceRepository;
import at.phactum.demo.insurance.persistence.InsuranceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final CustomerRepository customerRepository;

    public void saveInsurance(UUID customerId, String insuranceType, String insuranceCoverage, String insuranceSum) {
        final Customer customer = customerRepository.findById(customerId).orElseThrow();
        Insurance insurance = new Insurance();
        insurance.setInsuranceType(InsuranceType.valueOf(insuranceType));
        insurance.setInsuranceNumber(generateInsuranceNumber());
        insurance.setName(insuranceType + "-" + insurance.getInsuranceNumber() + "-" + customer.getCustomerNumber());
        insurance.setSimpleName("simple");
        insurance.setCreatedAt(LocalDateTime.now());
        insurance.setInsuranceCoverage(insuranceCoverage);
        insurance.setInsuranceSum(insuranceSum);
        insuranceRepository.save(insurance);
        customer.getInsurances().add(insurance);
        customerRepository.save(customer);
    }

    private int generateInsuranceNumber() {
        final long insuranceCount = insuranceRepository.count();
        return Long.valueOf(insuranceCount + 1).intValue();
    }


}
