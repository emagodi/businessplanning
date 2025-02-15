package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.payload.request.DepartmentRequest;
import zw.co.zetdc.businessplanning.repository.DepartmentRepository;
import zw.co.zetdc.businessplanning.service.DepartmentService;

import java.lang.reflect.Method;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository; // Repository for database operations

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
}