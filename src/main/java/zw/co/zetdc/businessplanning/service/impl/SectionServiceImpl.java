package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.*;
import zw.co.zetdc.businessplanning.payload.request.SectionRequest;  // Make sure to import the SectionRequest payload
import zw.co.zetdc.businessplanning.repository.DivisionRepository;
import zw.co.zetdc.businessplanning.repository.SectionRepository;  // Make sure to import the Section repository
import zw.co.zetdc.businessplanning.repository.TeamMemberRepository;
import zw.co.zetdc.businessplanning.service.DepartmentService;
import zw.co.zetdc.businessplanning.service.DivisionService;
import zw.co.zetdc.businessplanning.service.SectionService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository; // Repository for database operations

    private final DepartmentService departmentService;

    private final TeamMemberRepository teamMemberRepository;

    private final DivisionRepository divisionRepository;


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
    public List<Section> getSectionsByDivision(Long divisionId) {
        // Fetch the division and its associated departments
        Division division = divisionRepository.findById(divisionId).orElse(null);
        if (division != null) {
            List<Section> sections = new ArrayList<>();
            for (Department department : division.getAssignedDepartments()) {
                sections.addAll(department.getAssignedSections());
            }
            return sections;
        }
        return Collections.emptyList(); // Return an empty list if division is not found
    }


}