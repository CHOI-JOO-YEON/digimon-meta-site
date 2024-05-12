package com.joo.digimon.report.repository;

import com.joo.digimon.report.model.Report;
import com.joo.digimon.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Integer> {

    @EntityGraph("Report.detail")
    List<Report> findByUser(User user);


    @EntityGraph("Report.detail")
    @Override
    Optional<Report> findById(Integer id);
}
