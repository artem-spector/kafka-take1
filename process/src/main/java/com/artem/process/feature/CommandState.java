package com.artem.process.feature;

/**
 * TODO: Document!
 *
 * @author artem on 15/05/2017.
 */
public class CommandState {

    public String command;
    public long sentAt;
    public long respondedAt;
    public int progress;
    public String error;

    public boolean inProgress() {
        return (sentAt > 0) && (error == null) && (respondedAt == 0 || progress < 100);
    }
}
