package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkPlanSummaryResponse {
    private Long divisionId;
    private String divisionName;
    private String week;
    private String month;
    private String year;
    private int totalWorkPlans;
    private int totalOverdue;
    private double totalBudget;
    private double totalExpenditure;
    private String currency;
    private List<DepartmentSummaryResponse> departments;
}