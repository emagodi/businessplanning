package zw.co.zetdc.businessplanning.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.enums.Currency;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.enums.Unit;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkPlanResponse {

    private String id;

    private String month;
    private String week;
    private String year;
    private String planName;
    private Integer weeklyTarget;
    private Integer actualWorkDone;
    private Double budget;
    private Double percentageComplete;
    private Double actualExpenditure;
    private Double percentOfBudget;
    private String remarks;
    private Status status;
    private Unit unit;
    private Currency currency;
    private Date startDate;
    private Date targetCompletionDate;
    private Date actualCompletionDate;

    private Long sectionId;
    private Long departmentId;
    private Long divisionId;

    private List<Scope> scopes;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

}
