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
@Table(name = "groups")
@EntityListeners(AuditingEntityListener.class)
public class DepartmentGroup extends BaseEntity {
    private String name;

    @ManyToMany
    @JoinTable(
            name = "group_department",
            joinColumns = @JoinColumn(name = "department_group_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private List<Department> assignedDepartments = new ArrayList<>();
}
