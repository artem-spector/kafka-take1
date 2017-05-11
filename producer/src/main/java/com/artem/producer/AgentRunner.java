package com.artem.producer;

import com.artem.server.AgentJVM;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/6/17
 */
public class AgentRunner {

    private final AgentMock agentMock;
    private final KafkaTopicProducer producer;
    private volatile boolean stopIt;
    private Thread thread;

    public static void main(String[] args) throws IOException {
        KafkaTopicProducer producer = new KafkaTopicProducer("process-in-topic");
        int numAgents = 2;

        AgentRunner agents[] = new AgentRunner[numAgents];
        for (int i = 0; i < numAgents; i++) agents[i] = new AgentRunner(i, producer);
        for (int i = 0; i < numAgents; i++) agents[i].go(1000);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignore
        }

        for (int i = 0; i < numAgents; i++) agents[i].stop();
    }

    public AgentRunner(int numAgent, KafkaTopicProducer producer) {
        AgentJVM key = new AgentJVM("AccountOne", "AgentOne", String.valueOf(numAgent));
        agentMock = new AgentMock(key);
        this.producer = producer;
    }

    private void go(long sleepInterval) {
        thread = new Thread() {
            @Override
            public void run() {
                while (!stopIt) {
                    try {
                        producer.send(agentMock.getKey(), agentMock.getData());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        Thread.sleep(sleepInterval);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    private void stop() {
        stopIt = true;
        thread.interrupt();
    }
}
