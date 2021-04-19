package cz.sknot.kafka.connect.googlesource;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleSheetsSourceConnector extends SourceConnector {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleSheetsSourceConnector.class);
    public static final String RANGE = "range";
    public static final String SPREAD_SHEET_ID = "spreadSheetId";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String TOPIC = "topics";
    public static final String DELAY = "delay";
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(RANGE, ConfigDef.Type.STRING, "0", ConfigDef.Importance.HIGH, "The range of cells to fetch.")
            .define(SPREAD_SHEET_ID, ConfigDef.Type.STRING, "0", ConfigDef.Importance.HIGH, "The id of the Spread Sheet.")
            .define(ACCESS_TOKEN, ConfigDef.Type.STRING, "0", ConfigDef.Importance.HIGH, "Access token to access Spread Sheet.")
            .define(DELAY, ConfigDef.Type.INT, 10000, ConfigDef.Importance.HIGH, "Delay between polls")
            .define(TOPIC, ConfigDef.Type.STRING, "0", ConfigDef.Importance.HIGH, "The topic to send the records.");

    private String range;
    private String spreadSheetId;
    private String accessToken;
    private String topic;
    private Integer delay;

    @Override
    public String version() {
        return "0.0.8";
    }

    @Override
    public void start(Map<String, String> props) {
        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, props);
        range = parsedConfig.getString(RANGE);
        spreadSheetId = parsedConfig.getString(SPREAD_SHEET_ID);
        accessToken = parsedConfig.getString(ACCESS_TOKEN);
        topic = parsedConfig.getString(TOPIC);
        delay = parsedConfig.getInt(DELAY);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return GoogleSheetsSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();

        Map<String, String> config = new HashMap<>(6);

        if (spreadSheetId != null) {
            config.put(SPREAD_SHEET_ID, spreadSheetId);
        }

        if (range != null) {
            config.put(RANGE, range);
        }

        if (topic != null) {
            config.put(TOPIC, topic);
        }

        if (accessToken != null) {
            config.put(ACCESS_TOKEN, accessToken);
        }

        if (delay != null) {
            config.put(DELAY, Integer.toString(delay));
        }

        for (int i = 0; i < maxTasks; i++) {
            configs.add(config);
        }
        return configs;
    }

    @Override
    public void stop() {
        // Nothing to do
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }
}
