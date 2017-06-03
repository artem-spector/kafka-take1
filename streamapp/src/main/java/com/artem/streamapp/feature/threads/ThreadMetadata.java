package com.artem.streamapp.feature.threads;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 31/05/2017
 */
public class ThreadMetadata {

    public String threadId;
    public Thread.State threadState;
    public List<MethodCall> stackTrace;

    public ThreadMetadata() {
    }

    public ThreadMetadata(Thread.State threadState, List<MethodCall> stackTrace) {
        this.threadState = threadState;
        this.stackTrace = stackTrace;

        try {
            MessageDigest digest = MessageDigest.getInstance("sha-1");
            for (MethodCall call : stackTrace) {
                digest.update(call.declaringClass.getBytes());
                digest.update(call.methodName.getBytes());
                digest.update(call.fileName.getBytes());
                digest.update(String.valueOf(call.lineNumber).getBytes());
            }
            threadId = new String(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
