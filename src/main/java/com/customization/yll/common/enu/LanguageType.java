package com.customization.yll.common.enu;

public enum LanguageType {
    CN("简体中文"),
    EN("English");
    private String name;

    LanguageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
