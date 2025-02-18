package zw.co.zetdc.businessplanning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.enums.Status;

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
    private Double weeklyTarget;
    private Double actualWorkDone;
    private Double percentageComplete;
    private Double actualExpenditure;
    private Double percentOfBudget;
    private String remarks;

    private Status status;
    private Date startDate;
    private Date targetCompletionDate;
    private Date actualCompletionDate;

    private Long sectionId;
    private Long departmentId;

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
