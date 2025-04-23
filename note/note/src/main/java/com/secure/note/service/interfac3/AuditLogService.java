package com.secure.note.service.interfac3;

import com.secure.note.entity.AuditLog;
import com.secure.note.entity.Note;

import java.util.List;


public interface AuditLogService {
    void logNoteCreation(String username, Note note);

    void logNoteUpdate(String username, Note note);

    void logNoteDeletion(String username, Long noteId);

    List<AuditLog> getAllAuditLogs();

    List<AuditLog> getAuditLogsForNoteId(Long id);
}
