package zw.co.zetdc.businessplanning.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.zetdc.businessplanning.handlers.BaseEntity;

import java.util.List;

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

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY) // Relationship with Section
    private List<Section> sections; // List of sections associated with this department


}
