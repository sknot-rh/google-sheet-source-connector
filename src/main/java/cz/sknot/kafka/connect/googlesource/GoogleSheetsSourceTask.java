package cz.sknot.kafka.connect.googlesource;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GoogleSheetsSourceTask extends SourceTask {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleSheetsSourceTask.class);

    private String spreadSheetId;
    private String range;
    private String accessToken;
    private String topic;
    private Integer delay;

    @Override
    public String version() {
        return new GoogleSheetsSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        range = props.get(GoogleSheetsSourceConnector.RANGE);
        spreadSheetId = props.get(GoogleSheetsSourceConnector.SPREAD_SHEET_ID);
        accessToken = props.get(GoogleSheetsSourceConnector.ACCESS_TOKEN);
        topic = props.get(GoogleSheetsSourceConnector.TOPIC);
        delay = Integer.parseInt(props.get(GoogleSheetsSourceConnector.DELAY));
    }

    @Override
    public List<SourceRecord> poll() {
        try {
            Thread.sleep(delay);
            Response res = fetchDataFromApi();
            String tex = res.body().string();
            res.close();
            return Collections.singletonList(buildSourceRecord(tex));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Response response = client.newCall(request).execute();

        return response;
    }


}