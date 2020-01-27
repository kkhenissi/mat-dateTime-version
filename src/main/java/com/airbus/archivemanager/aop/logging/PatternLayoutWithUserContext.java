package com.airbus.archivemanager.aop.logging;


import ch.qos.logback.classic.PatternLayout;

public class PatternLayoutWithUserContext extends PatternLayout {
    static {
        PatternLayout.defaultConverterMap.put(
            "user", UserConverter.class.getName());
    }
    static {
        PatternLayout.defaultConverterMap.put(
            "role", RoleConverter.class.getName());
    }
}
