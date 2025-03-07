package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DepartmentWorkPlanSummaryResponse {

    private Long departmentId;
    private Long totalWorkPlans;
    private Long completedWorkPlans;
    private Long inProgressWorkPlans;
    private Long cancelledWorkPlans;
    private Long rescheduledWorkPlans;
    private Double averagePercentOfBudgetUtilized;
    private Double overallCompletionRate;

}
