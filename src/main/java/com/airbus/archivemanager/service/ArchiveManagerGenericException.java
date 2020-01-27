package com.airbus.archivemanager.service;

public class ArchiveManagerGenericException extends RuntimeException {

    public ArchiveManagerGenericException(String message) {

        super(message, null, false, false);
    }
}
