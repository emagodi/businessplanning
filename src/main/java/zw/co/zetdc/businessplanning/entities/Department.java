package zw.co.zetdc.businessplanning.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.zetdc.businessplanning.handlers.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "departments")
@EntityListeners(AuditingEntityListener.class)
public class Department extends BaseEntity {

    private String name;


    @ManyToMany
    @JoinTable(
            name = "department_section",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private List<Section> assignedSections = new ArrayList<>();
}
