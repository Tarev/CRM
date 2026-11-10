package com.example.crm.service;

import com.example.crm.model.Note;
import com.example.crm.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService extends AbstractService<Note> {

    public NoteService(EntityRepository<Note> repository) {
        super(repository);
    }

    public List<Note> findByDealId(Long dealId) {
        List<Note> result = new ArrayList<>();
        for (Note note : findAll()) {
            if (dealId != null && dealId.equals(note.getDealId())) {
                result.add(note);
            }
        }
        return result;
    }
}
