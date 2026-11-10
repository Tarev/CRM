package com.example.crm.model;

import com.example.crm.util.Validation;

import java.time.LocalDate;

public class Task extends BaseEntity {

    private final String title;
    private final Long dealId;
    /** id пользователя-менеджера, которому назначена задача. */
    private final Long assigneeId;
    private final LocalDate deadline;
    private boolean done;

    public Task(String title, Long dealId, Long assigneeId, LocalDate deadline) {
        this.title = Validation.requireNotBlank(title, "title");
        this.dealId = Validation.requireNotNull(dealId, "dealId");
        this.assigneeId = Validation.requireNotNull(assigneeId, "assigneeId");
        this.deadline = Validation.requireNotNull(deadline, "deadline");
        this.done = false;
    }

    public String getTitle() {
        return title;
    }

    public Long getDealId() {
        return dealId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Задача #" + getId() + ": " + title + ", сделка " + dealId + ", исполнитель " + assigneeId
                + ", срок " + deadline + ", " + (done ? "выполнена" : "не выполнена");
    }
}
