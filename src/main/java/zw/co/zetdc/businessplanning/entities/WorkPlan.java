package zw.co.zetdc.businessplanning.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "plans")
@EntityListeners(AuditingEntityListener.class)
public class WorkPlan extends BaseEntity {

    private String month;
    private String week;

    private Double weeklyTarget;
    private Double actualWorkDone;
    private Double percentageComplete;
    private Double actualExpenditure;
    private Double percentOfBudget;
    private String remarks;

    private Long sectionId;
    private Long departmentId;

    @OneToMany(mappedBy = "workPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Scope> scopes = new ArrayList<>(); // Initialize the list

}
