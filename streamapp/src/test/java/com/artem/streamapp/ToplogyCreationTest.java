package com.artem.streamapp;

import com.artem.server.AgentJVM;
import com.artem.server.Features;
import com.artem.streamapp.base.KafkaIntegrationTestBase;
import com.artem.streamapp.base.StreamsApplication;
import com.artem.streamapp.base.TestStreamsApplication;
import com.artem.streamapp.ext.ActiveAgentProcessor;
import com.artem.streamapp.feature.LoadDataProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;

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
public class ToplogyCreationTest extends KafkaIntegrationTestBase {

    private String appId;

    public ToplogyCreationTest() {
        super("ToplogyCreationTest");
    }

    @Override
    protected StreamsApplication createApplication(String appId) {
        this.appId = appId;
        return new TestStreamsApplication(appId, topologyProperties, earliest)
                .addSource(INPUT_SOURCE_ID, IN_TOPIC)
                .addSink(COMMANDS_SINK_ID, COMMAND_OUT_TOPIC)
                .addProcessors(ActiveAgentProcessor.class, LoadDataProcessor.class);
    }

    @Test
    public void testStartStop() throws InterruptedException {
        System.out.println("in the test, wait...");
        Thread.sleep(500);
    }

    @Test
    public void testLoadDataProducer() {
        ConsumerRecords<AgentJVM, Map<String, Map<String, Object>>> commands = pollCommands(100);
        assertEquals(0, commands.count());

        AgentJVM key = new AgentJVM("testAccount", "testAgent", "1");
        HashMap<String, Object> value = new HashMap<>();
        value.put("sentAt", System.currentTimeMillis());
        value.put("sentForApp", appId);
        sendInputRecord(key, value);
        commands = pollCommands(5000);
        assertNotNull(commands);
        Map<String, Map<String, Object>> agentCommands = extractAgentCommands(commands, key);
        Map<String, Object> metricsCommand = agentCommands.get(Features.JVM_METRICS);
        assertNotNull(metricsCommand);
        assertEquals("monitor", metricsCommand.get("command"));
    }

}
