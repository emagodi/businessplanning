package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.entities.WorkPlan;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkPlanScopeResponse {

    private WorkPlan workPlan;
    private Scope scope; // This is now a single Scope
    private List<TeamMember> assignedTeamMembers;

}
