package zw.co.zetdc.businessplanning.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.zetdc.businessplanning.handlers.BaseEntity;




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
//    private String teamMembers;
    private Double percentageComplete;
    private Double actualExpenditure;
    private Double percentOfBudget;
    private String remarks;


}
