package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberRequest;
import zw.co.zetdc.businessplanning.repository.TeamMemberRepository;
import zw.co.zetdc.businessplanning.service.TeamMemberService;

import java.lang.reflect.Method;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository; // Repository for database operations

    @Override
    @Transactional
    public TeamMember createTeamMember(TeamMemberRequest teamMemberRequest) {
        TeamMember teamMember = TeamMember.builder()
                .firstname(teamMemberRequest.getFirstname())
                .lastname(teamMemberRequest.getLastname())
                .email(teamMemberRequest.getEmail())
                .ecNumber(teamMemberRequest.getEcNumber())
                .designation(teamMemberRequest.getDesignation())
                .build();

        return teamMemberRepository.save(teamMember); // Save to database
    }

    @Override
    public TeamMember getTeamMemberById(Long id) {
        return teamMemberRepository.findById(id)
                .orElse(null); // Return null if not found
    }

    @Override
    public List<TeamMember> getAllTeamMembers() {
        return teamMemberRepository.findAll(); // Fetch all from database
    }

    @Override
    @Transactional
    public TeamMember updateTeamMember(Long id, TeamMemberRequest teamMemberRequest) {
        TeamMember teamMember = getTeamMemberById(id);
        if (teamMember != null) {
            copyNonNullProperties(teamMemberRequest, teamMember);
            return teamMemberRepository.save(teamMember); // Save updated team member
        }
        return null; // Return null if not found
    }

    @Override
    @Transactional
    public void deleteTeamMember(Long id) {
        teamMemberRepository.deleteById(id); // Delete from database
    }

    private void copyNonNullProperties(TeamMemberRequest source, TeamMember target) {
        for (Method method : TeamMemberRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = TeamMember.class.getDeclaredMethod(setterName, method.getReturnType());
                        setter.invoke(target, value);
                    }
                } catch (Exception e) {
                    log.error("Error copying properties: {}", e.getMessage(), e); // Better logging
                }
            }
        }
    }


}
