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
public class EmailAboveBudgetWorkPlan {

    private String planName;
    private List<String> teamMemberNames;
    private Double budget;
    private Double actualExpenditure;
    private Double difference;
    private String currency;
}
