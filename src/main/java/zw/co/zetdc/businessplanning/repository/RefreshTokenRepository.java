package zw.co.zetdc.businessplanning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.zetdc.businessplanning.entities.RefreshToken;


import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

}
