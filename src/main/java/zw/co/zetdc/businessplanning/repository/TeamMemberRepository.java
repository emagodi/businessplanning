package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.zetdc.businessplanning.entities.TeamMember;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findBySectionId(Long sectionId);
    List<TeamMember> findByDepartmentId(Long departmentId);

}
