package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.zetdc.businessplanning.entities.Section;
import zw.co.zetdc.businessplanning.entities.WorkPlan;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByDepartmentsId(Long departmentId);

    @Query("""
        SELECT s FROM Section s 
        JOIN s.departments d 
        WHERE d.id = :departmentId
    """)
    List<Section> findByDepartmentId(@Param("departmentId") Long departmentId);

}
