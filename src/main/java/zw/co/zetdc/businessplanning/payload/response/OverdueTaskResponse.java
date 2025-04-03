package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.zetdc.businessplanning.entities.WorkPlan;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OverdueTaskResponse {
    private WorkPlan workPlan;
    private String managerEmail;
    private String seniorManagerEmail;

}