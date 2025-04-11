package zw.co.zetdc.businessplanning.payload.response;

import lombok.Data;

@Data
public class SectionSummaryResponse {
    private Long sectionId;
    private String sectionName;
    private int year;
    private int totalWorkPlans;
    private int completedWorkPlans;
    private int pendingWorkPlans;
    private double totalBudgetUSD;
    private double totalActualUsedUSD;
    private double totalBudgetZWL;
    private double totalActualUsedZWL;
    private String sectionHeadEmail;
    private String firstname;
    private String lastname;
}