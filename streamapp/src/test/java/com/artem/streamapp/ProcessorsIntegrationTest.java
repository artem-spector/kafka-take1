package com.artem.streamapp;

import com.artem.producer.features.FeatureDataProducer;
import com.artem.producer.features.LiveThreadsProducer;
import com.artem.producer.features.LoadDataProducer;
import com.artem.server.AgentJVM;
import com.artem.server.Features;
import com.artem.streamapp.base.KafkaIntegrationTestBase;
import com.artem.streamapp.base.StreamsApplication;
import com.artem.streamapp.base.TestStreamsApplication;
import com.artem.streamapp.ext.ActiveAgentProcessor;
import com.artem.streamapp.feature.load.LoadDataProcessor;
import com.artem.streamapp.feature.threads.ThreadDumpProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.artem.streamapp.base.StreamsApplication.AutoOffsetReset.earliest;
import static com.artem.streamapp.ext.AgentFeatureProcessor.COMMANDS_SINK_ID;
import static com.artem.streamapp.ext.AgentFeatureProcessor.INPUT_SOURCE_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TODO: Document!
 *
 * @author artem on 23/05/2017.
 */
public class ProcessorsIntegrationTest extends KafkaIntegrationTestBase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorsIntegrationTest.class);

    private String appId;
    private LoadDataProducer loadDataProducer = new LoadDataProducer();
    private LiveThreadsProducer threadDumpProducer = new LiveThreadsProducer();

    public ProcessorsIntegrationTest() {
        super("ProcessorsIntegrationTest");
    }

    @Override
    protected StreamsApplication createApplication(String appId) {
        this.appId = appId;
        return new TestStreamsApplication(appId, topologyProperties, earliest)
                .addSource(INPUT_SOURCE_ID, IN_TOPIC)
                .addSink(COMMANDS_SINK_ID, COMMAND_OUT_TOPIC)
                .addProcessors(ActiveAgentProcessor.class, LoadDataProcessor.class, ThreadDumpProcessor.class);
    }

    @Test
    public void testStartStop() throws InterruptedException {
        System.out.println("in the test, wait...");
        Thread.sleep(500);
    }

    @Test
    public void testLoadDataProcessor() throws InterruptedException {
        ConsumerRecords<AgentJVM, Map<String, Map<String, Object>>> commands = pollCommands(1000);
        assertEquals(0, commands.count());

        AgentJVM key = new AgentJVM("testAccount", "testAgent", "1");
        produceInput(key, loadDataProducer);

        Map<String, Object> metricsCommand = awaitCommand(key, Features.JVM_METRICS, 5000);
        assertEquals("monitor", metricsCommand.get("command"));

        loadDataProducer.setCommand((String) metricsCommand.get("command"), (Map<String, Object>) metricsCommand.get("param"));
        int count = 15;
        logger.info("sending " + count + " load requests");
        for (int i = 0; i < count; i++) {
            produceInput(key, loadDataProducer);
        }

        // send an event that crosses punctuation boundary to trigger punctuate
        Thread.sleep(1000);
        produceInput(key, loadDataProducer);

        logger.info("------------------ test end");
    }

    @Test
    public void testThreadDumpProcessor() throws InterruptedException {
        ConsumerRecords<AgentJVM, Map<String, Map<String, Object>>> commands = pollCommands(1000);
        assertEquals(0, commands.count());

        AgentJVM key = new AgentJVM("testAccount", "testAgent", "1");
        produceInput(key, loadDataProducer);

        Map<String, Object> dumpCommand = awaitCommand(key, Features.LIVE_THREADS, 5000);
        assertEquals("dump", dumpCommand.get("command"));

        threadDumpProducer.setCommand((String) dumpCommand.get("command"), (Map<String, Object>) dumpCommand.get("param"));
        produceInput(key, threadDumpProducer);

        // send an event that crosses punctuation boundary to trigger punctuate
        Thread.sleep(1000);
        produceInput(key, loadDataProducer);
        logger.info("------------------ test end");
    }

    private void produceInput(AgentJVM key, FeatureDataProducer... producers) {
        HashMap<String, Object> value = new HashMap<>();
        value.put("sentAt", System.currentTimeMillis());
        value.put("sentForApp", appId);

        for (FeatureDataProducer producer : producers) {
            value.put(producer.featureId, producer.getFeatureData());
        }

        sendInputRecord(key, value);
    }

    private Map<String, Object> awaitCommand(AgentJVM key, String featureId, long timeout) {
        ConsumerRecords<AgentJVM, Map<String, Map<String, Object>>> commands = pollCommands(timeout);
        assertNotNull("Command not received during " + timeout + " ms.", commands);
        Map<String, Map<String, Object>> agentCommands = extractAgentCommands(commands, key);
        Map<String, Object> featureCommand = agentCommands.get(featureId);
        assertNotNull("No command received for feature " + featureId, featureCommand);
        return featureCommand;
    }

}
