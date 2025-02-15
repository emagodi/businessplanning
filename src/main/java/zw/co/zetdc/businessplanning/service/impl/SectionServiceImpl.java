package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.entities.Section;  // Make sure to import the Section entity
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.payload.request.SectionRequest;  // Make sure to import the SectionRequest payload
import zw.co.zetdc.businessplanning.repository.SectionRepository;  // Make sure to import the Section repository
import zw.co.zetdc.businessplanning.repository.TeamMemberRepository;
import zw.co.zetdc.businessplanning.service.DepartmentService;
import zw.co.zetdc.businessplanning.service.SectionService;

import java.lang.reflect.Method;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository; // Repository for database operations

    private final DepartmentService departmentService;

    private final TeamMemberRepository teamMemberRepository;


    @Override
    @Transactional
    public Section createSection(SectionRequest sectionRequest) {
        Section section = Section.builder()
                .name(sectionRequest.getName())
                .build();

        return sectionRepository.save(section); // Save to database
    }

    @Override
    public Section getSectionById(Long id) {
        return sectionRepository.findById(id)
                .orElse(null); // Return null if not found
    }

    @Override
    public List<Section> getAllSections() {
        return sectionRepository.findAll(); // Fetch all from database
    }

    @Override
    @Transactional
    public Section updateSection(Long id, SectionRequest sectionRequest) {
        Section section = getSectionById(id);
        if (section != null) {
            copyNonNullProperties(sectionRequest, section);
            return sectionRepository.save(section); // Save updated section
        }
        return null; // Return null if not found
    }

    @Override
    @Transactional
    public void deleteSection(Long id) {
        sectionRepository.deleteById(id); // Delete from database
    }

    private void copyNonNullProperties(SectionRequest source, Section target) {
        for (Method method : SectionRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = Section.class.getDeclaredMethod(setterName, method.getReturnType());
                        setter.invoke(target, value);
                    }
                } catch (Exception e) {
                    log.error("Error copying properties: {}", e.getMessage(), e); // Better logging
                }
            }
        }
    }

    @Override
    @Transactional
    public void assignSectionsToDepartment(Long departmentId, List<Long> sectionIds) {
        Department department = departmentService.getDepartmentById(departmentId); // Get the department
        if (department != null) {
            List<Section> sections = sectionRepository.findAllById(sectionIds); // Fetch sections by IDs
            for (Section section : sections) {
                section.setDepartment(department); // Assign department to each section
            }
            sectionRepository.saveAll(sections); // Save updated sections
        }
    }

    @Override
    @Transactional
    public void assignTeamMembersToSection(Long sectionId, List<Long> teamMemberIds) {
        Section section = getSectionById(sectionId); // Get the section
        if (section != null) {
            List<TeamMember> teamMembers = teamMemberRepository.findAllById(teamMemberIds); // Fetch team members by IDs
            for (TeamMember member : teamMembers) {
                member.setSection(section); // Assign section to each team member
            }
            teamMemberRepository.saveAll(teamMembers); // Save updated team members
        }
    }
}