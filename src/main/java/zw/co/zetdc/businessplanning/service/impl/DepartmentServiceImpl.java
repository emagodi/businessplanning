package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.entities.Section;
import zw.co.zetdc.businessplanning.exception.NotFoundException;
import zw.co.zetdc.businessplanning.payload.request.DepartmentRequest;
import zw.co.zetdc.businessplanning.payload.request.SectionIdsRequest;
import zw.co.zetdc.businessplanning.repository.DepartmentRepository;
import zw.co.zetdc.businessplanning.repository.SectionRepository;
import zw.co.zetdc.businessplanning.service.DepartmentService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository; // Repository for database operations

    private final SectionRepository sectionRepository;

    @Override
    @Transactional
    public Department createDepartment(DepartmentRequest departmentRequest) {
        Department department = Department.builder()
                .name(departmentRequest.getName())
                .build();

        return departmentRepository.save(department); // Save to database
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElse(null); // Return null if not found
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll(); // Fetch all from database
    }

    @Override
    @Transactional
    public Department updateDepartment(Long id, DepartmentRequest departmentRequest) {
        Department department = getDepartmentById(id);
        if (department != null) {
            copyNonNullProperties(departmentRequest, department);
            return departmentRepository.save(department); // Save updated department
        }
        return null; // Return null if not found
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id); // Delete from database
    }

    private void copyNonNullProperties(DepartmentRequest source, Department target) {
        for (Method method : DepartmentRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = Department.class.getDeclaredMethod(setterName, method.getReturnType());
                        setter.invoke(target, value);
                    }
                } catch (Exception e) {
                    log.error("Error copying properties: {}", e.getMessage(), e); // Better logging
                }
            }
        }
    }

    @Override
    public Department getDepartmentWithSections(Long departmentId) {
        return departmentRepository.findById(departmentId).orElse(null);
    }

    @Override
    @Transactional
    public List<Section> addSectionsToDepartment(Long departmentId, SectionIdsRequest request) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NotFoundException("Department with id:: " + departmentId + " not found"));

        // Initialize the assignedSections list if it's not already done
        if (department.getAssignedSections() == null) {
            department.setAssignedSections(new ArrayList<>());
        }

        // Adding sections based on IDs received in the request
        for (Long sectionId : request.getSectionIds()) {
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new NotFoundException("Section with id:: " + sectionId + " not found"));
            department.getAssignedSections().add(section);
        }

        // Save the department to persist the changes
        departmentRepository.save(department);

        return department.getAssignedSections(); // Return the updated list of sections
    }


}