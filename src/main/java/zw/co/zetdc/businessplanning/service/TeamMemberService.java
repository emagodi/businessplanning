package zw.co.zetdc.businessplanning.service;


import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberRequest;

import java.util.List;

public interface TeamMemberService {


    TeamMember createTeamMember(TeamMemberRequest teamMemberRequest);

    TeamMember getTeamMemberById(Long id);

    List<TeamMember> getAllTeamMembers();

    TeamMember updateTeamMember(Long id, TeamMemberRequest teamMemberRequest);

    void deleteTeamMember(Long id);

    public List<TeamMember> getTeamMembersBySectionId(Long sectionId);

    public List<TeamMember> getTeamMembersByDepartmentId(Long departmentId);

}
