package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OverdueEmailResponse {
    private String sectionName;
    private Long sectionId;
    private int totalWorkPlans;
    private int overdueWorkPlans;
    private double percentageOverdue;
    private String sectionManager;
    private List<String> teamMembers;
}
