package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.zetdc.businessplanning.entities.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
