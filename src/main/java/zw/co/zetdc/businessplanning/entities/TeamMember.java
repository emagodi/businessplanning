package zw.co.zetdc.businessplanning.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "members")
@EntityListeners(AuditingEntityListener.class)
public class TeamMember extends BaseEntity {

    private String firstname;
    private String lastname;
    private String email;
    private String ecNumber;
    private String designation;

    @ManyToMany(mappedBy = "assignedTeamMembers")
    @JsonIgnore // Prevents this relationship from being serialized
    private List<Scope> scopes;

    @ManyToOne // Each team member belongs to one section
    @JoinColumn(name = "section_id") // Foreign key column in TeamMember table
    @JsonIgnore // Prevents this relationship from being serialized
    private Section section; // Reference to the associated section


}
