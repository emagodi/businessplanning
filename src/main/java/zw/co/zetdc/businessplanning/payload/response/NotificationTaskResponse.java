package zw.co.zetdc.businessplanning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationTaskResponse {
    private Long departmentId;
    private String departmentName;
    private String managerEmail;
    private String seniorManagerEmail;
    private Long sectionId;
    private String sectionName;
    private int overdueCount;
}
