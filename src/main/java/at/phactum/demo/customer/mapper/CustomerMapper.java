package at.phactum.demo.customer.mapper;

import java.util.List;

import at.phactum.demo.customer.dto.CustomerDto;
import at.phactum.demo.customer.persistence.Customer;
import at.phactum.demo.customer.dto.CreateCustomerForm;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer map(CreateCustomerForm createCustomerForm);

    CustomerDto map(Customer customer);

    List<CustomerDto> map(List<Customer> customers);

}
