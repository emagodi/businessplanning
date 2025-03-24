package zw.co.zetdc.businessplanning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.entities.DepartmentGroup;
import zw.co.zetdc.businessplanning.payload.request.DepartmentGroupRequest;
import zw.co.zetdc.businessplanning.payload.request.DepartmentIdsRequest;
import zw.co.zetdc.businessplanning.service.DepartmentGroupService;

import java.util.List;

@Tag(name = "DEPARTMENT GROUP ENDPOINTS", description = "The Department Group APIs. Contains operations like create group, find group by id, etc.")
@RestController
@RequestMapping("/api/department-groups")
@RequiredArgsConstructor
@Slf4j
public class DepartmentGroupController {

    private final DepartmentGroupService departmentGroupService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<DepartmentGroup> createDepartmentGroup(@RequestBody DepartmentGroupRequest groupRequest) {
        DepartmentGroup createdGroup = departmentGroupService.createDepartmentGroup(groupRequest);
        return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
    }

    @GetMapping(value = "/findById/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<DepartmentGroup> getDepartmentGroupById(@PathVariable Long id) {
        DepartmentGroup departmentGroup = departmentGroupService.getDepartmentGroupById(id);
        if (departmentGroup != null) {
            return new ResponseEntity<>(departmentGroup, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/findAll")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<DepartmentGroup>> getAllDepartmentGroups() {
        List<DepartmentGroup> groups = departmentGroupService.getAllDepartmentGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<DepartmentGroup> updateDepartmentGroup(@PathVariable Long id,
                                                                 @RequestBody DepartmentGroupRequest groupRequest) {
        DepartmentGroup updatedGroup = departmentGroupService.updateDepartmentGroup(id, groupRequest);
        if (updatedGroup != null) {
            return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Void> deleteDepartmentGroup(@PathVariable Long id) {
        departmentGroupService.deleteDepartmentGroup(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{groupId}/add/departments")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Add departments to group", description = "Add departments by their IDs to the specified department group")
    public ResponseEntity<List<Department>> addDepartmentsToGroup(
            @PathVariable("groupId") Long groupId,
            @RequestBody DepartmentIdsRequest request
    ) {
        List<Department> updatedDepartments = departmentGroupService.addDepartmentsToGroup(groupId, request);
        return new ResponseEntity<>(updatedDepartments, HttpStatus.OK);
    }
}