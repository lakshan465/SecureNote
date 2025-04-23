package com.secure.note.service;

// NoteServiceImpl.java


import com.secure.note.entity.Note;
import com.secure.note.repo.NoteRepository;
import com.secure.note.service.interfac3.AuditLogService;
import com.secure.note.service.interfac3.NoteService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NoteServiceImpl implements NoteService {


    private final NoteRepository noteRepository;
    private final AuditLogService auditLogService;

    @Override
    public Note createNoteForUser(String username, String content) {
        System.out.println(content);
        Note note = new Note();
        note.setContent(content);
        note.setOwnerUsername(username);
        note = noteRepository.save(note);
        auditLogService.logNoteCreation(username, note);
        return note;
    }

    @Override
    public Note updateNoteForUser(Long noteId, String content, String username) {
        Note note = noteRepository.findById(noteId).orElseThrow(()
                -> new RuntimeException("Note not found"));
        note.setContent(content);
        auditLogService.logNoteUpdate(username, note);
        return noteRepository.save(note);
    }

    @Override
    public void deleteNoteForUser(Long noteId, String username) {
        Note note = noteRepository.findById(noteId).orElseThrow(()-> new RuntimeException("Note not found"));
        noteRepository.deleteById(noteId);
        auditLogService.logNoteDeletion(username, noteId);
    }

    @Override
    public List<Note> getNotesForUser(String username) {

        return noteRepository
                .findByOwnerUsername(username);
    }
}

