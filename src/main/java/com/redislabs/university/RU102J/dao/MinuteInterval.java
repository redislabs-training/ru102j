package com.redislabs.university.RU102J.dao;

public enum MinuteInterval {
    ONE(1),
    FIVE(5),
    TEN(10),
    TWENTY(20),
    THIRTY(30),
    SIXTY(60);

    private final int value;

    MinuteInterval(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

