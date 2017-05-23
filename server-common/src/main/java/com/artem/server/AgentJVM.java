package com.artem.server;

import java.util.Arrays;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/7/17
 */
public class AgentJVM implements Comparable<AgentJVM> {

    public String accountId;
    public String agentId;
    public String jvmId;

    public AgentJVM() {
    }

    public AgentJVM(String accountId, String agentId, String jvmId) {
        this.accountId = accountId;
        this.agentId = agentId;
        this.jvmId = jvmId;
    }

    @Override
    public int hashCode() {
        return accountId.hashCode() << 2 + agentId.hashCode() << 1 + jvmId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || !(obj instanceof AgentJVM)) return false;

        AgentJVM that = (AgentJVM) obj;
        return Arrays.equals(new Object[]{accountId, agentId, jvmId}, new Object[]{that.accountId, that.agentId, that.jvmId});
    }

    @Override
    public String toString() {
        return "agentJVM: {accountId:" + accountId + ", agentId:" + agentId + ", jvmId:" + jvmId + "}";
    }

    @Override
    public int compareTo(AgentJVM that) {
        int res = accountId.compareTo(that.accountId);
        if (res == 0) res = agentId.compareTo(that.agentId);
        if (res == 0) res = jvmId.compareTo(that.jvmId);
        return res;
    }
}
