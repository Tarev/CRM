package com.example.crm.model;

import com.example.crm.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "done", nullable = false)
    private boolean done;

    /** Сделка, к которой относится задача. Может быть null (общая задача). */
    @ManyToOne
    @JoinColumn(name = "deal_id")
    private Deal deal;

    /** Исполнитель задачи. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;
}
