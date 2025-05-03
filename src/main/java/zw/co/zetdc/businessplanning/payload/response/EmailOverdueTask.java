package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmailOverdueTask {
    private Long workPlanId;
    private String planName;
    private List<String> teamMemberNames;
    private Date targetCompletionDate;
    private Long daysOverdue;
}
