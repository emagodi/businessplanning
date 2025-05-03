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
public class EmailSectionSummary {

    private String sectionName;
    private String sectionManagerFullName;
    private String sectionManagerEmail;
    private List<EmailOverdueTask> overdueTasks;
    private EmailTeamMemberSummary teamMemberWithHighestWorkPlans;
    private EmailTeamMemberSummary teamMemberWithLowestWorkPlans;
    private List<EmailAboveBudgetWorkPlan> aboveBudgetWorkPlans;

}
