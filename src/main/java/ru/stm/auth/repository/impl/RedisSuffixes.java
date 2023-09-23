package ru.stm.auth.repository.impl;

public enum RedisSuffixes {

    token(".token");

    private String suffix;

    RedisSuffixes(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
