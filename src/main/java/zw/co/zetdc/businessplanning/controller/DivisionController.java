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
import zw.co.zetdc.businessplanning.entities.Division;
import zw.co.zetdc.businessplanning.payload.request.DivisionRequest;
import zw.co.zetdc.businessplanning.payload.request.DepartmentIdsRequest;
import zw.co.zetdc.businessplanning.service.DivisionService;

import java.util.List;

@Tag(name = "DIVISION ENDPOINTS", description = "The Division APIs. Contains operations like create division, find division by id, etc.")
@RestController
@RequestMapping("/api/divisions")
@RequiredArgsConstructor
@Slf4j
public class DivisionController {

    private final DivisionService divisionService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Division> createDivision(@RequestBody DivisionRequest divisionRequest) {
        Division createdDivision = divisionService.createDivision(divisionRequest);
        return new ResponseEntity<>(createdDivision, HttpStatus.CREATED);
    }

    @GetMapping(value = "/findById/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Division> getDivisionById(@PathVariable Long id) {
        Division division = divisionService.getDivisionById(id);
        if (division != null) {
            return new ResponseEntity<>(division, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/findAll")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Division>> getAllDivisions() {
        List<Division> divisions = divisionService.getAllDivisions();
        return new ResponseEntity<>(divisions, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Division> updateDivision(@PathVariable Long id,
                                                   @RequestBody DivisionRequest divisionRequest) {
        Division updatedDivision = divisionService.updateDivision(id, divisionRequest);
        if (updatedDivision != null) {
            return new ResponseEntity<>(updatedDivision, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Void> deleteDivision(@PathVariable Long id) {
        divisionService.deleteDivision(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{divisionId}/add/departments")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "Add departments to division", description = "Add departments by their IDs to the specified division")
    public ResponseEntity<List<Department>> addDepartmentsToDivision(
            @PathVariable("divisionId") Long divisionId,
            @RequestBody DepartmentIdsRequest request
    ) {
        List<Department> updatedDepartments = divisionService.addDepartmentsToDivision(divisionId, request);
        return new ResponseEntity<>(updatedDepartments, HttpStatus.OK);
    }

    @GetMapping("/{id}/departments")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    @Operation(summary = "List of departments in the division", description = "List of departments in the division")
    public ResponseEntity<Division> getDivisionWithDepartments(@PathVariable Long id) {
        Division division = divisionService.getDivisionWithDepartments(id);
        return division != null ? ResponseEntity.ok(division) : ResponseEntity.notFound().build();
    }
}