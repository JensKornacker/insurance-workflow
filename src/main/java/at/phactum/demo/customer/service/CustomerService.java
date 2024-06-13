package at.phactum.demo.customer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import at.phactum.demo.customer.dto.CustomerDto;
import at.phactum.demo.customer.persistence.Customer;
import at.phactum.demo.customer.dto.CreateCustomerForm;
import at.phactum.demo.customer.mapper.CustomerMapper;
import at.phactum.demo.customer.persistence.CustomerRepository;
import at.phactum.demo.insurance.persistence.Insurance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final int CUSTOMER_NUMBER_START = 54321;

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerDto saveCustomer(CreateCustomerForm createCustomerForm) {
        Customer customer = customerMapper.map(createCustomerForm);
        customer.setCustomerNumber(generateCustomerNumber());
        customer.setCreatedAt(LocalDateTime.now());
        return customerMapper.map(customerRepository.save(customer));
    }

    public List<CustomerDto> getCustomers() {
        return customerMapper.map(customerRepository.findAll());
    }

    @Transactional
    public CustomerDto getCustomer(UUID id) {
        final Customer customer = customerRepository.findById(id)
                                                    .orElseThrow(null);
        return customerMapper.map(customer);
    }

    private int generateCustomerNumber() {
        final long customersCount = customerRepository.count();
        return CUSTOMER_NUMBER_START + Long.valueOf(customersCount).intValue();
    }

}
