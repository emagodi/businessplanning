package zw.co.zetdc.businessplanning.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberRequest;
import zw.co.zetdc.businessplanning.service.TeamMemberService;

import java.util.List;

@Tag(name = "TASK TEAM MEMBERS ENDPOINTS", description = "The Task Team Member APIs. Contains operations like create member, find member by id etc.")
@RestController
@RequestMapping("/api/teamMembers")
@RequiredArgsConstructor
@Slf4j
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<TeamMember> createTeamMember(@RequestBody TeamMemberRequest teamMemberRequest) {
        TeamMember createdTeamMember = teamMemberService.createTeamMember(teamMemberRequest);
        return new ResponseEntity<>(createdTeamMember, HttpStatus.CREATED);
    }

    @GetMapping(value = "/findById/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<TeamMember> getTeamMemberById(@PathVariable Long id) {
        TeamMember teamMember = teamMemberService.getTeamMemberById(id);
        if (teamMember != null) {
            return new ResponseEntity<>(teamMember, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/findAll")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<TeamMember>> getAllTeamMembers() {
        List<TeamMember> teamMembers = teamMemberService.getAllTeamMembers();
        return new ResponseEntity<>(teamMembers, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<TeamMember> updateTeamMember(@PathVariable Long id,
                                                       @RequestBody TeamMemberRequest teamMemberRequest) {
        TeamMember updatedTeamMember = teamMemberService.updateTeamMember(id, teamMemberRequest);
        if (updatedTeamMember != null) {
            return new ResponseEntity<>(updatedTeamMember, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        teamMemberService.deleteTeamMember(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<TeamMember>> getTeamMembersBySectionId(@PathVariable Long sectionId) {
        List<TeamMember> teamMembers = teamMemberService.getTeamMembersBySectionId(sectionId);
        return ResponseEntity.ok(teamMembers);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<TeamMember>> getTeamMembersByDepartmentId(@PathVariable Long departmentId) {
        List<TeamMember> teamMembers = teamMemberService.getTeamMembersByDepartmentId(departmentId);
        return ResponseEntity.ok(teamMembers);
    }
}