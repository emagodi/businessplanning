package zw.co.zetdc.businessplanning.payload.response;

import java.time.LocalDateTime;

public class TeamMemberResponse {

    private Long id;

    private String firstname;
    private String lastname;
    private String email;
    private String ecNumber;
    private String designation;

    private Long sectionId;
    private Long departmentId;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;


}
