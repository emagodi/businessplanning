package zw.co.zetdc.businessplanning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.enums.Status;


import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScopeRequest {

    private String details;
    private Status status;
    private Date startDate;
    private Date targetCompletionDate;
    private Date actualCompletionDate;
    private List<Long> assignedTeamMemberIds;
}
