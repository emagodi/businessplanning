package zw.co.zetdc.businessplanning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScopeRequest {

    private String details;
    private List<Long> assignedTeamMemberIds;
}
