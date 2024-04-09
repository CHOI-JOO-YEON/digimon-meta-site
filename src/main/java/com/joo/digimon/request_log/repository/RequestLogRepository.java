package com.joo.digimon.request_log.repository;

import com.joo.digimon.request_log.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog,Long> {
}
