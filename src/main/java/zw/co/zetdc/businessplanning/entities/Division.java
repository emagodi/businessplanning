package zw.co.zetdc.businessplanning.entities;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.zetdc.businessplanning.handlers.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "divisions")
@EntityListeners(AuditingEntityListener.class)
public class Division extends BaseEntity {
    private String name;

    @ManyToMany
    @JoinTable(
            name = "division_department",
            joinColumns = @JoinColumn(name = "division_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private List<Department> assignedDepartments = new ArrayList<>();
}
