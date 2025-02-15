package zw.co.zetdc.businessplanning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignTeamMembersRequest {
    private List<Long> teamMemberIds; // List of team member IDs to assign
}
