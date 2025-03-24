package zw.co.zetdc.businessplanning.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.zetdc.businessplanning.enums.Currency;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.handlers.BaseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plans")
@EntityListeners(AuditingEntityListener.class)
public class WorkPlan extends BaseEntity {

    private String month;
    private String week;
    private String year;

    private Integer weeklyTarget;
    private Integer actualWorkDone;
    private Double percentageComplete;
    private Double budget;
    private Double actualExpenditure;
    private Double percentOfBudget;

    private Currency currency;


    private Status status;
    private Date startDate;
    private Date targetCompletionDate;
    private Date actualCompletionDate;

    private String remarks;

    private Long sectionId;
    private Long departmentId;
    private Long departmentGroupId;

    @OneToMany(mappedBy = "workPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Scope> scopes = new ArrayList<>(); // Initialize the list


    public void updatePercentageComplete() {
        if (weeklyTarget != null && weeklyTarget > 0 && actualWorkDone != null) {
            this.percentageComplete = (actualWorkDone.doubleValue() / weeklyTarget.doubleValue()) * 100;
        } else {
            this.percentageComplete = 0.0; // Default to 0 if targets aren't set correctly
        }
    }

    public void updatePercentOfBudget() {
        if (budget != null && budget > 0 && actualExpenditure != null) {
            this.percentOfBudget = (actualExpenditure / budget) * 100;
        } else {
            this.percentOfBudget = 0.0; // Default to 0 if budget is not set correctly
        }
    }

}
