package GoogleSheetsSourceConnector;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class TaskClass extends SourceTask {
    private static final Logger LOG = LoggerFactory.getLogger(TaskClass.class);
    private final ObjectMapper mapper = new ObjectMapper();


    private String spreadSheetId;
    private String range;
    private String accessToken;
    private String topic;

    @Override
    public String version() {
        return new GoogleSheetsSource().version();
    }

    @Override
    public void start(Map<String, String> props) {
        range = props.get(GoogleSheetsSource.RANGE);
        spreadSheetId = props.get(GoogleSheetsSource.SPREAD_SHEET_ID);
        accessToken = props.get(GoogleSheetsSource.ACCESS_TOKEN);
        topic = props.get(GoogleSheetsSource.TOPIC);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        try {
            Response res = fetchDataFromApi();
            LOG.info("Recieved {}", res.body().string());
            JsonObject jo = new JsonObject();
            jo.put("values", res.body().string());
            buildSourceRecord(jo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SourceRecord buildSourceRecord(Object event) {
        return new SourceRecord(null, null, topic, SchemaBuilder.struct(), event);
    }

    @Override
    public void stop() {
        // Nothing to do
    }

    private Response fetchDataFromApi() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://sheets.googleapis.com/v4/spreadsheets/" + spreadSheetId + "/values/" + range)
                .get()
                .addHeader("Authorization", accessToken)
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }


}