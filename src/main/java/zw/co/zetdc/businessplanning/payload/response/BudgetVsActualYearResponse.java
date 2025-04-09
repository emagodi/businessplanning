package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BudgetVsActualYearResponse {
    private String year;
    private Long divisionId;
    private List<MonthlyBudgetVsActual> monthlyData;

    @Data
    @AllArgsConstructor
    public static class MonthlyBudgetVsActual {
        private String month;
        private Double budgetUSD;
        private Double actualUSD;
        private Double differenceUSD;
        private Double budgetZWL;
        private Double actualZWL;
        private Double differenceZWL;
    }
}
