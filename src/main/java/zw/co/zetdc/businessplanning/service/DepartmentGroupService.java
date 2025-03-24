package zw.co.zetdc.businessplanning.service;

import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.entities.DepartmentGroup;
import zw.co.zetdc.businessplanning.payload.request.DepartmentGroupRequest;
import zw.co.zetdc.businessplanning.payload.request.DepartmentIdsRequest;

import java.util.List;

public interface DepartmentGroupService {

    DepartmentGroup createDepartmentGroup(DepartmentGroupRequest groupRequest);

    DepartmentGroup getDepartmentGroupById(Long id);

    List<DepartmentGroup> getAllDepartmentGroups();

    DepartmentGroup updateDepartmentGroup(Long id, DepartmentGroupRequest groupRequest);

    void deleteDepartmentGroup(Long id);

    List<Department> addDepartmentsToGroup(Long groupId, DepartmentIdsRequest request);
}
