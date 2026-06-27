package edu.awieclawski.app.dao;

import edu.awieclawski.app.model.ErrorMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessageEntity, Integer> {

    @Query("SELECT e FROM ErrorMessageEntity e WHERE e.userLogin = :user AND e.createdAt BETWEEN :start AND :end")
    List<ErrorMessageEntity> findByCreatedAtBetween(@Param("user") String userLogin,
                                                    @Param("start") Timestamp startDate,
                                                    @Param("end") Timestamp endDate);
}
