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

    private int weeklyTarget;
    private int actualWorkDone;
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

    @OneToMany(mappedBy = "workPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Scope> scopes = new ArrayList<>(); // Initialize the list


    public void setPercentageComplete(Double percentageComplete) {
        this.percentageComplete = percentageComplete;

        // If percentageComplete is 100%, update the status of all associated scopes to COMPLETED
        if (this.percentageComplete != null && this.percentageComplete == 100) {
            for (Scope scope : this.scopes) {
                scope.setStatus(Status.COMPLETED);
            }
        }
    }

}
