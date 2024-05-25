package at.phactum.demo.customer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import at.phactum.demo.customer.dto.CustomerDto;
import at.phactum.demo.customer.persistence.Customer;
import at.phactum.demo.customer.dto.CreateCustomerForm;
import at.phactum.demo.customer.mapper.CustomerMapper;
import at.phactum.demo.customer.persistence.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerDto saveCustomer(CreateCustomerForm createCustomerForm) {
        Customer customer = customerMapper.map(createCustomerForm);
        customer.setCreatedAt(LocalDateTime.now());
        return customerMapper.map(customerRepository.save(customer));
    }

    public List<CustomerDto> getCustomers() {
        return customerMapper.map(customerRepository.findAll());
    }

    public CustomerDto getCustomer(UUID id) {
        return customerMapper.map(customerRepository.findById(id)
                                                    .orElseThrow(null));
    }

}