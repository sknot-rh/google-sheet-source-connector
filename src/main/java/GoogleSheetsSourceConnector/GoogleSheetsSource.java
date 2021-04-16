package GoogleSheetsSourceConnector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.connector.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleSheetsSource extends SinkConnector {
    private static final Logger LOG = LoggerFactory.getLogger(SinkConnector.class);
    public static final String RANGE = "range";
    public static final String SPREAD_SHEET_ID = "spreadSheetId";
    public static final String ACCESS_TOKEN = "acessToken";
    public static final String TOPIC = "topics";
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(RANGE, ConfigDef.Type.STRING, "0", ConfigDef.Importance.HIGH, "The range of cells to fetch.")
            .define(SPREAD_SHEET_ID, ConfigDef.Type.STRING, "0", ConfigDef.Importance.HIGH, "The id of the Spread Sheet.")
            .define(ACCESS_TOKEN, ConfigDef.Type.STRING, "0", ConfigDef.Importance.HIGH, "Access token to access Spread Sheet.")
            .define(TOPIC, ConfigDef.Type.STRING, "0", ConfigDef.Importance.HIGH, "The topic to send the records.");

    private String range;
    private String spreadSheetId;
    private String accessToken;
    private String topic;

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public void start(Map<String, String> props) {
        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, props);
        range = parsedConfig.getString(RANGE);
        spreadSheetId = parsedConfig.getString(SPREAD_SHEET_ID);
        accessToken = parsedConfig.getString(ACCESS_TOKEN);
        topic = parsedConfig.getString(TOPIC);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return TaskClass.class;
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