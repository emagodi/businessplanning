package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamMemberSummaryResponse {

    private Long teamMemberId;
    private String firstname;
    private String lastname;
    private Integer totalWorkPlans;
    private Integer completed;
    private Integer inProgress;
    private Integer overdue;
    private Integer cancelled;
    private Integer reScheduled;


}
