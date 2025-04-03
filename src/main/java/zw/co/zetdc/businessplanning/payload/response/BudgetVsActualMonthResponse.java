package zw.co.zetdc.businessplanning.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BudgetVsActualMonthResponse {

    private String week;
    private Double budget;
    private Double actual;
    private Double difference;
    private Double percentageDifference;

}
