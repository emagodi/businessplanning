package zw.co.zetdc.businessplanning.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zw.co.zetdc.businessplanning.entities.Section;  // Make sure to import the Section entity
import zw.co.zetdc.businessplanning.payload.request.AssignSectionsRequest;
import zw.co.zetdc.businessplanning.payload.request.AssignTeamMembersRequest;
import zw.co.zetdc.businessplanning.payload.request.SectionRequest;  // Make sure to import the SectionRequest payload
import zw.co.zetdc.businessplanning.service.SectionService;

import java.util.List;

@Tag(name = "SECTION ENDPOINTS", description = "The Section APIs. Contains operations like create section, find section by id, etc.")
@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
@Slf4j
public class SectionController {

    private final SectionService sectionService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Section> createSection(@RequestBody SectionRequest sectionRequest) {
        Section createdSection = sectionService.createSection(sectionRequest);
        return new ResponseEntity<>(createdSection, HttpStatus.CREATED);
    }

    @GetMapping(value = "/findById/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Section> getSectionById(@PathVariable Long id) {
        Section section = sectionService.getSectionById(id);
        if (section != null) {
            return new ResponseEntity<>(section, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/findAll")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<List<Section>> getAllSections() {
        List<Section> sections = sectionService.getAllSections();
        return new ResponseEntity<>(sections, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Section> updateSection(@PathVariable Long id,
                                                 @RequestBody SectionRequest sectionRequest) {
        Section updatedSection = sectionService.updateSection(id, sectionRequest);
        if (updatedSection != null) {
            return new ResponseEntity<>(updatedSection, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{departmentId}/assignSections")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'SENIORMANAGER', 'MANAGER', 'HOD', 'USER')")
    public ResponseEntity<Void> assignSectionsToDepartment(@PathVariable Long departmentId,
                                                           @RequestBody AssignSectionsRequest request) {
        sectionService.assignSectionsToDepartment(departmentId, request.getSectionIds());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 No Content on success
    }

}