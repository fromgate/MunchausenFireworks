package me.fromgate.munchausen.commands;


import me.fromgate.munchausen.message.M;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CmdDefine {
    String command();

    String[] subCommands();

    String permission();

    boolean allowConsole() default false;

    M description();

    String shortDescription();
}

