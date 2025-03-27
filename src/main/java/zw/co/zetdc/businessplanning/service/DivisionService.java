package zw.co.zetdc.businessplanning.service;

import zw.co.zetdc.businessplanning.entities.Department;
import zw.co.zetdc.businessplanning.entities.Division;
import zw.co.zetdc.businessplanning.payload.request.DivisionRequest;
import zw.co.zetdc.businessplanning.payload.request.DepartmentIdsRequest;

import java.util.List;

public interface DivisionService {


    Division createDivision(DivisionRequest divisionRequest);


    Division getDivisionById(Long id);


    List<Division> getAllDivisions();


    Division updateDivision(Long id, DivisionRequest divisionRequest);


    void deleteDivision(Long id);


    List<Department> addDepartmentsToDivision(Long divisionId, DepartmentIdsRequest request);
}