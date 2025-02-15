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
@Table(name = "sections")
@EntityListeners(AuditingEntityListener.class)
public class Section extends BaseEntity {

    private String name;

    @ManyToOne // Many sections can belong to one department
    @JoinColumn(name = "department_id") // Foreign key column in Section table
    private Department department; // Reference to the associated department


    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY) // Each section can have multiple team members
    private List<TeamMember> assignedTeamMembers; // List of team members assigned to this section


}
