package com.example.crm.service;

import com.example.crm.dto.NoteRequest;
import com.example.crm.model.Deal;
import com.example.crm.model.Note;
import com.example.crm.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService extends AbstractService<Note> {

    private final NoteRepository noteRepository;
    private final DealService dealService;

    public NoteService(NoteRepository noteRepository, DealService dealService) {
        super(noteRepository);
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

    /** Создает заметку. Возвращает empty, если сделка не найдена. */
    public Optional<Note> create(NoteRequest request) {
        Optional<Deal> deal = dealService.findById(request.getDealId());
        if (deal.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(save(new Note(deal.get(), request.getText())));
    }

    /** Обновляет заметку (дата создания не меняется). Возвращает empty, если заметка или сделка не найдены. */
    public Optional<Note> update(Long id, NoteRequest request) {
        Optional<Note> note = findById(id);
        Optional<Deal> deal = dealService.findById(request.getDealId());
        if (note.isEmpty() || deal.isEmpty()) {
            return Optional.empty();
        }
        note.get().setDeal(deal.get());
        note.get().setText(request.getText());
        return Optional.of(save(note.get()));
    }
}
