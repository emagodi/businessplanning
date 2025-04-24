package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SectionWorkPlanSummaryResponsePeriod {

    private Long sectionId;
    private String sectionName;
    private String managerFirstname;
    private String managerLastname;
    private Long totalCount;
    private Long completed;
    private Long pending;
    private Long inProgress;
    private Long cancelled;
    private Long reScheduled;
    private Long overdue;

}
