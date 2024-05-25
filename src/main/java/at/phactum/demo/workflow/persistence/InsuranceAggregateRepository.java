package at.phactum.demo.workflow.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceAggregateRepository extends JpaRepository<InsuranceAggregate, String> {

}
