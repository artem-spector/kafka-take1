package com.artem.streamapp;

import com.artem.streamapp.base.StreamsApplication;
import com.artem.streamapp.ext.ActiveAgentProcessor;
import com.artem.streamapp.feature.classinfo.ClassInfoProcessor;
import com.artem.streamapp.feature.load.LoadDataProcessor;
import com.artem.streamapp.feature.threads.ThreadDumpProcessor;

import java.io.IOException;
import java.util.Properties;

import static com.artem.streamapp.ext.AgentFeatureProcessor.COMMANDS_SINK_ID;
import static com.artem.streamapp.ext.AgentFeatureProcessor.INPUT_SOURCE_ID;

/**
 * TODO: Document!
 *
 * @author artem
 *         Date: 03/06/2017
 */
public class AppRunner {

    public static final String IN_TOPIC = "process-in-topic";
    public static final String COMMAND_OUT_TOPIC = "command-topic";

    public static void main(String[] args) throws IOException {
        Properties topologyProperties = new Properties();
        topologyProperties.load(AppRunner.class.getClassLoader().getResourceAsStream("export.properties"));

        StreamsApplication app = new StreamsApplication("processingApp", topologyProperties, StreamsApplication.AutoOffsetReset.latest)
                .addSource(INPUT_SOURCE_ID, IN_TOPIC)
                .addSink(COMMANDS_SINK_ID, COMMAND_OUT_TOPIC)
                .addProcessors(ActiveAgentProcessor.class, LoadDataProcessor.class, ThreadDumpProcessor.class, ClassInfoProcessor.class);

        app.build().start();
    }
}
