package zw.co.zetdc.businessplanning.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.handlers.BaseEntity;

import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scope")
@EntityListeners(AuditingEntityListener.class)
public class Scope extends BaseEntity {

    private String details;
    private Status status;
    private Date startDate;
    private Date targetCompletionDate;
    private Date actualCompletionDate;

    @ManyToOne
    @JoinColumn(name = "work_plan_id")
    @JsonBackReference // Prevents infinite recursion during serialization
    private WorkPlan workPlan;

    @ManyToMany
    @JoinTable(
            name = "scope_team_member",
            joinColumns = @JoinColumn(name = "scope_id"),
            inverseJoinColumns = @JoinColumn(name = "team_member_id")
    )
    private List<TeamMember> assignedTeamMembers = new ArrayList<>(); // Initialize the list

}
