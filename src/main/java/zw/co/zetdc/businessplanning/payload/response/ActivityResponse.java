package zw.co.zetdc.businessplanning.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.entities.TeamMember;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActivityResponse {

    private Long id;

    private String activityName;
    private Double weeklyTarget;
    private Double actualWorkDone;
    private Double percentageComplete;
    private Double actualExpenditure;
    private Double percentOfBudget;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;


}
