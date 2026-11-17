package com.example.crm.service;

import com.example.crm.dto.NoteRequest;
import com.example.crm.model.Deal;
import com.example.crm.model.Note;
import com.example.crm.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService extends AbstractService<Note> {

    private final NoteRepository noteRepository;
    private final DealService dealService;

    public NoteService(NoteRepository noteRepository, DealService dealService) {
        super(noteRepository, "Заметка");
        this.noteRepository = noteRepository;
        this.dealService = dealService;
    }

    /** Если id сделки не указан, возвращает все заметки. */
    public List<Note> findByDealId(Long dealId) {
        if (dealId == null) {
            return findAll();
        }
        return noteRepository.findByDealIdOrderByCreatedAtAsc(dealId);
    }

    /** Создает заметку. */
    public Note create(NoteRequest request) {
        Deal deal = dealService.requireReference(request.getDealId());
        return save(new Note(deal, request.getText()));
    }

    /** Обновляет заметку (дата создания не меняется). */
    public Note update(Long id, NoteRequest request) {
        Note note = getById(id);
        Deal deal = dealService.requireReference(request.getDealId());
        note.setDeal(deal);
        note.setText(request.getText());
        return save(note);
    }
}
