package zw.co.zetdc.businessplanning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.entities.Scope;

import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkPlanRequest {
    private String month;
    private String week;
    private List<ScopeRequest> scopes; // List of scopes

    @Data
    public static class ScopeRequest {
        private Long id; // Optional for existing scopes
        private String details;
        private List<Long> assignedTeamMemberIds; // List of team member IDs
    }
}
