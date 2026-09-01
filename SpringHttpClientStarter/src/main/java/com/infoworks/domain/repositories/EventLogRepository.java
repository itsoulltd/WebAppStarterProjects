package com.infoworks.domain.repositories;

import com.infoworks.domain.entities.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {
}
