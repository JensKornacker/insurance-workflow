package at.phactum.insurance.workflow.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceAggregateRepository extends JpaRepository<InsuranceAggregate, String> {

}
