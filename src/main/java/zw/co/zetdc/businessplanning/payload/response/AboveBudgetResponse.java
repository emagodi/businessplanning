package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboveBudgetResponse {
    private String sectionName;
    private Long sectionId;
    private Double totalBudget;
    private Double actualExpenditure;
    private Double percentageAboveBudget;
    private String sectionManager;
    private String seniorManagerEmail;
    private String sectionManagerEmail;
}
