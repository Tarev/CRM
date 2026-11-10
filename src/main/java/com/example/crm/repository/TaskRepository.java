package com.example.crm.repository;

import com.example.crm.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** Невыполненные задачи, срок которых раньше указанной даты. */
    List<Task> findByDoneFalseAndDeadlineBefore(LocalDate date);

    /** Фильтры необязательные: если параметр null, он не учитывается. */
    @Query("select t from Task t where (:assigneeId is null or t.assignee.id = :assigneeId)"
            + " and (:dealId is null or t.deal.id = :dealId)"
            + " and (:done is null or t.done = :done)")
    List<Task> search(@Param("assigneeId") Long assigneeId,
                      @Param("dealId") Long dealId,
                      @Param("done") Boolean done);
}
