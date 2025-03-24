package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.entities.DepartmentGroup;
import zw.co.zetdc.businessplanning.exception.NotFoundException;
import zw.co.zetdc.businessplanning.payload.request.DepartmentGroupRequest;
import zw.co.zetdc.businessplanning.payload.request.DepartmentIdsRequest;
import zw.co.zetdc.businessplanning.repository.DepartmentGroupRepository;
import zw.co.zetdc.businessplanning.repository.DepartmentRepository;
import zw.co.zetdc.businessplanning.service.DepartmentGroupService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentGroupServiceImpl implements DepartmentGroupService {

    private final DepartmentGroupRepository departmentGroupRepository; // Repository for database operations

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DepartmentGroup createDepartmentGroup(DepartmentGroupRequest groupRequest) {
        DepartmentGroup departmentGroup = DepartmentGroup.builder()
                .name(groupRequest.getName())
                .build();

        return departmentGroupRepository.save(departmentGroup); // Save to database
    }

    @Override
    public DepartmentGroup getDepartmentGroupById(Long id) {
        return departmentGroupRepository.findById(id)
                .orElse(null); // Return null if not found
    }

    @Override
    public List<DepartmentGroup> getAllDepartmentGroups() {
        return departmentGroupRepository.findAll(); // Fetch all from database
    }

    @Override
    @Transactional
    public DepartmentGroup updateDepartmentGroup(Long id, DepartmentGroupRequest groupRequest) {
        DepartmentGroup departmentGroup = getDepartmentGroupById(id);
        if (departmentGroup != null) {
            copyNonNullProperties(groupRequest, departmentGroup);
            return departmentGroupRepository.save(departmentGroup); // Save updated department group
        }
        return null; // Return null if not found
    }

    @Override
    @Transactional
    public void deleteDepartmentGroup(Long id) {
        departmentGroupRepository.deleteById(id); // Delete from database
    }

    private void copyNonNullProperties(DepartmentGroupRequest source, DepartmentGroup target) {
        for (Method method : DepartmentGroupRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = DepartmentGroup.class.getDeclaredMethod(setterName, method.getReturnType());
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
    public List<Department> addDepartmentsToGroup(Long groupId, DepartmentIdsRequest request) {
        DepartmentGroup departmentGroup = departmentGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("DepartmentGroup with id:: " + groupId + " not found"));

        // Initialize the assignedDepartments list if it's not already done
        if (departmentGroup.getAssignedDepartments() == null) {
            departmentGroup.setAssignedDepartments(new ArrayList<>());
        }

        // Adding departments based on IDs received in the request
        for (Long departmentId : request.getDepartmentIds()) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new NotFoundException("Department with id:: " + departmentId + " not found"));
            departmentGroup.getAssignedDepartments().add(department);
        }

        // Save the department group to persist the changes
        departmentGroupRepository.save(departmentGroup);

        return departmentGroup.getAssignedDepartments(); // Return the updated list of departments
    }
}