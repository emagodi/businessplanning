package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DepartmentSummaryResponse {
    private Long departmentId;
    private String departmentName;
    private int workPlansCount;
    private int overdueTasks;
    private double percentageComplete;
    private double percentagePending;
    private double percentageInProgress;
    private double percentageCancelled;
    private double percentageReScheduled;
    private double departmentBudget;
    private double percentageBudget;
    private double percentageOverdue;
}