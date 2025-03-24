package zw.co.zetdc.businessplanning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamMemberRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String ecNumber;
    private String designation;

    private Long sectionId;
    private Long departmentId;

    private Long departmentGroupId;
}
