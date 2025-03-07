package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SectionWorkPlanPerformanceResponse {

    private Long sectionId;
    private String quarter;
    private int year;
    private Long totalWorkPlans;
    private Long completedWorkPlans;
    private Long inProgressWorkPlans;
    private Long cancelledWorkPlans;
    private Long rescheduledWorkPlans;
    private Double averagePercentOfBudgetUtilized;
    private Double overallCompletionRate;

}
