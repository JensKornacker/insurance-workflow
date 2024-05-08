package at.phactum.insurance.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
@Data
public class AbstractCrud {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    protected UUID id;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

}
