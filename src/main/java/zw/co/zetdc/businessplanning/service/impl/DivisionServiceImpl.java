package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.entities.Division;
import zw.co.zetdc.businessplanning.exception.NotFoundException;
import zw.co.zetdc.businessplanning.payload.request.DivisionRequest;
import zw.co.zetdc.businessplanning.payload.request.DepartmentIdsRequest;
import zw.co.zetdc.businessplanning.repository.DivisionRepository;
import zw.co.zetdc.businessplanning.repository.DepartmentRepository;
import zw.co.zetdc.businessplanning.service.DivisionService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DivisionServiceImpl implements DivisionService {

    private final DivisionRepository divisionRepository; // Repository for database operations

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public Division createDivision(DivisionRequest divisionRequest) {
        Division division = Division.builder()
                .name(divisionRequest.getName())
                .build();

        return divisionRepository.save(division); // Save to database
    }

    @Override
    public Division getDivisionById(Long id) {
        return divisionRepository.findById(id)
                .orElse(null); // Return null if not found
    }

    @Override
    public List<Division> getAllDivisions() {
        return divisionRepository.findAll(); // Fetch all from database
    }

    @Override
    @Transactional
    public Division updateDivision(Long id, DivisionRequest divisionRequest) {
        Division division = getDivisionById(id);
        if (division != null) {
            copyNonNullProperties(divisionRequest, division);
            return divisionRepository.save(division); // Save updated division
        }
        return null; // Return null if not found
    }

    @Override
    @Transactional
    public void deleteDivision(Long id) {
        divisionRepository.deleteById(id); // Delete from database
    }

    private void copyNonNullProperties(DivisionRequest source, Division target) {
        for (Method method : DivisionRequest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String setterName = "set" + method.getName().substring(3);
                        Method setter = Division.class.getDeclaredMethod(setterName, method.getReturnType());
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
    public List<Department> addDepartmentsToDivision(Long divisionId, DepartmentIdsRequest request) {
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new NotFoundException("Division with id:: " + divisionId + " not found"));

        // Initialize the assignedDepartments list if it's not already done
        if (division.getAssignedDepartments() == null) {
            division.setAssignedDepartments(new ArrayList<>());
        }

        // Adding departments based on IDs received in the request
        for (Long departmentId : request.getDepartmentIds()) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new NotFoundException("Department with id:: " + departmentId + " not found"));
            division.getAssignedDepartments().add(department);
        }

        // Save the division to persist the changes
        divisionRepository.save(division);

        return division.getAssignedDepartments(); // Return the updated list of departments
    }

    @Override
    public Division getDivisionWithDepartments(Long divisionId) {
        return divisionRepository.findById(divisionId).orElse(null);
    }
}