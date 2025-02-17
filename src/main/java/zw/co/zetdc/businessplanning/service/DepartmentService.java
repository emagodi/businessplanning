package zw.co.zetdc.businessplanning.service;

import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.entities.Section;
import zw.co.zetdc.businessplanning.entities.TeamMember;
import zw.co.zetdc.businessplanning.payload.request.DepartmentRequest;
import zw.co.zetdc.businessplanning.payload.request.SectionIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberIdsRequest;
import zw.co.zetdc.businessplanning.payload.request.TeamMemberRequest;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(DepartmentRequest departmentRequest);

    Department getDepartmentById(Long id);

    List<Department> getAllDepartments();

    Department updateDepartment(Long id, DepartmentRequest departmentRequest);

    void deleteDepartment(Long id);

    public Department getDepartmentWithSections(Long departmentId);

    public List<Section> addSectionsToDepartment(Long departmentId, SectionIdsRequest request);

}
