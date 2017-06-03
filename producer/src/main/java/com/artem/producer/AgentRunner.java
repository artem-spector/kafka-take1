package com.artem.producer;

import com.artem.server.AgentJVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 5/6/17
 */
public class AgentRunner {

    private static final Logger logger = LoggerFactory.getLogger(AgentRunner.class);

    private final AgentMock agentMock;
    private final KafkaTopicProducer producer;
    private CommandsTopicConsumer consumer;
    private volatile boolean stopIt;
    private Thread thread;

    public static void main(String[] args) throws IOException {
        KafkaTopicProducer producer = new KafkaTopicProducer("process-in-topic");
        CommandsTopicConsumer consumer = new CommandsTopicConsumer("command-topic", "agents-runner");
        int numAgents = 1;

        AgentRunner agents[] = new AgentRunner[numAgents];
        for (int i = 0; i < numAgents; i++) agents[i] = new AgentRunner(i, producer, consumer);
        for (int i = 0; i < numAgents; i++) agents[i].go(1000);
    }

    public AgentRunner(int numAgent, KafkaTopicProducer producer, CommandsTopicConsumer consumer) {
        this.consumer = consumer;
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
                        // request
                        AgentJVM key = agentMock.getKey();
                        Map<String, Object> data = agentMock.getData();
                        logger.info("sending request " + key + "->" + data);
                        producer.send(key, data);
                        // response
                        Map<String, Map<String, Object>> featureCommands = consumer.getFeatureCommands(key);
                        logger.info("got commands: " + featureCommands);
                        agentMock.setCommands(featureCommands);
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
