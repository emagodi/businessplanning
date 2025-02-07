package zw.co.zetdc.businessplanning.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.zetdc.businessplanning.handlers.BaseEntity;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activities")
@EntityListeners(AuditingEntityListener.class)
public class Activity extends BaseEntity {

    private String activityName;
    private Double weeklyTarget;
    private Double actualWorkDone;
    private Double percentageComplete;
    private Double actualExpenditure;
    private Double percentOfBudget;
    private String remarks;

    @ManyToMany
    @JoinTable(
            name = "activity_team_member", // This is the junction table name
            joinColumns = @JoinColumn(name = "activity_id"), // Foreign key for Activity
            inverseJoinColumns = @JoinColumn(name = "team_member_id") // Foreign key for TeamMember
    )
    private List<TeamMember> assignedTeamMembers;

}
