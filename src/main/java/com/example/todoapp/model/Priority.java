package com.example.todoapp.model;

public enum Priority {
    LOW("Thấp"),
    NORMAL("Bình thường"),
    HIGH("Cao"),
    URGENT("Khẩn cấp");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}