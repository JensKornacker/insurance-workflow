package at.phactum.demo.customer.controller;

import java.util.List;
import java.util.UUID;

import at.phactum.demo.customer.dto.CustomerDto;
import at.phactum.demo.customer.service.CustomerService;
import at.phactum.demo.customer.dto.CreateCustomerForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CreateCustomerForm createCustomerForm) {
        return ResponseEntity.ok(customerService.saveCustomer(createCustomerForm));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping("{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable("id")UUID id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

}
