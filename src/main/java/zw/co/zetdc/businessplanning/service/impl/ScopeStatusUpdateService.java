package zw.co.zetdc.businessplanning.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.Scope;
import zw.co.zetdc.businessplanning.enums.Status;
import zw.co.zetdc.businessplanning.repository.ScopeRepository;

import java.time.Instant;
import java.util.List;

@Service
public class ScopeStatusUpdateService {

    @Autowired
    private ScopeRepository scopeRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void updateScopeStatuses() {
        List<Scope> allScopes = scopeRepository.findAll();

        for (Scope scope : allScopes) {
            if (scope.getStartDate() != null && scope.getStartDate().toInstant().isBefore(Instant.now())) {
                if (scope.getStatus() == Status.PENDING) {
                    scope.setStatus(Status.IN_PROGRESS);
                }
            }
        }

        scopeRepository.saveAll(allScopes);
    }
}
