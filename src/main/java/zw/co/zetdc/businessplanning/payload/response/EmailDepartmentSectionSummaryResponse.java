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
public class EmailDepartmentSectionSummaryResponse {
    private Long departmentId;
    private String departmentName;
    private String seniorManagerFullName;
    private String seniorManagerEmail;
    private List<EmailSectionSummary> sections;
}
