package zw.co.zetdc.businessplanning.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActivityRequest {
    private String activityName;
    private Double weeklyTarget;
    private Double actualWorkDone;
    //    private String teamMembers;
    private Double percentageComplete;
    private Double actualExpenditure;
    private Double percentOfBudget;
    private String remarks;

}
