package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BudgetVsActualCurrencyResponse {
    private String month;
    private String year;
    private Long divisionId;
    private List<WeeklyBudgetVsActual> weeklyData;

    @Data
    @AllArgsConstructor
    public static class WeeklyBudgetVsActual {
        private String week;
        private Double budgetUSD;
        private Double actualUSD;
        private Double differenceUSD;
        private Double budgetZWL;
        private Double actualZWL;
        private Double differenceZWL;
    }
}
