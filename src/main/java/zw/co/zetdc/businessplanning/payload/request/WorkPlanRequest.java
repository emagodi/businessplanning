package zw.co.zetdc.businessplanning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.enums.Currency;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.enums.Unit;

import java.util.Date;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkPlanRequest {
    private String month;
    private String week;
    private String year;
    private String planName;
    private Integer weeklyTarget;
    private Integer actualWorkDone;
//    private Double percentageComplete;
    private Double budget;
    private Double actualExpenditure;
//    private Double percentOfBudget;
    private Currency currency;
    private Unit unit;
    private String remarks;
    private Status status;
    private Date startDate;
    private Date targetCompletionDate;
    private Date actualCompletionDate;

    private Long sectionId;
    private Long departmentId;
    private Long divisionId;

    private List<ScopeRequest> scopes; // List of scopes

    @Data
    public static class ScopeRequest {
        private Long id; // Optional for existing scopes
        private String details;
        private Status status;
        private Date startDate;
        private Date targetCompletionDate;
        private Date actualCompletionDate;
        private List<Long> assignedTeamMemberIds; // List of team member IDs
    }
}
