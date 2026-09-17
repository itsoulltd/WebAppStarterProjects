package com.infoworks.services.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.data.impl.SimpleDataSource;
import com.infoworks.objects.Message;
import com.infoworks.objects.MessageParser;
import com.infoworks.objects.Response;
import com.infoworks.orm.Row;
import com.infoworks.tasks.ExecutableTask;
import com.infoworks.utils.excel.writer.AsyncWriter;
import com.infoworks.utils.excel.writer.StreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReportWriter extends ExecutableTask<Message, Response> {

    private static Logger LOG = LoggerFactory.getLogger(ReportWriter.class);
    private Map<String, Object> data;
    private String saveDir;
    private ObjectMapper mapper;

    public ReportWriter(Map<String, Object> data, String saveDir, ObjectMapper mapper) {
        this.data = data;
        this.saveDir = saveDir;
        this.mapper = mapper;
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        Map<String, Object> data = new HashMap<>();
        data.putAll(this.data);

        //Writing to output-file:
        String filename = data.get("filename").toString();
        String fileSavePath = Path.of(saveDir, filename).toString();
        try (AsyncWriter writer = new StreamWriter(100, fileSavePath)) {
            //Prepare Data: (Sheet-01)
            String[] headers = {"AccountName","Currency","Amount","Balance","Type","Date","Ref"};
            Map<Integer, List<String>> headerRow = new HashMap<>();
            headerRow.put(0, Arrays.asList(headers));
            writer.write("data", headerRow);

            String[] colKeys = {"account_ref","currency","amount","balance","transaction_type","transaction_date","transaction_ref"};
            SimpleDataSource<Integer, Map<String, Object>> dataSource = dataSource();

            //Write to xlsx file:
            AtomicInteger indexCounter = new AtomicInteger(1);
            pagination(dataSource, 5, -1, (result) -> {
                int startIndex = indexCounter.get();
                Map<Integer, List<String>> reportData = AsyncWriter.convert(result, startIndex, colKeys); // startIndex need move page by page.
                writer.write("data", reportData);
                indexCounter.addAndGet(reportData.size());
            });
            //END:: Sheet-01

            //Prepare Data: (Sheet-02)
            headerRow = new HashMap<>();
            headerRow.put(0, Arrays.asList("Metric", "Sum"));
            writer.write("summary", headerRow);
            //Write summary:
            List<Map<String, Object>> summary = getDummySummary("metric", "sum");
            writer.write("summary", AsyncWriter.convert(summary, 1, "metric", "sum"));
            //END:: Sheet-02

            //Flush any leftover:
            writer.flush();
            data.put("status", "COMPLETE");
        } catch (Exception e) {
            data.put("status", "FAILED");
            data.put("reason", e.getMessage());
        }

        //CAUTION: Making some delay for testing, please remove in production.
        try { Thread.sleep(5000); } catch (Exception ignore) {}
        //...
        return new Response().setStatus(200).setMessage(MessageParser.printJson(data, mapper));
    }

    private List<Map<String, Object>> getDummySummary(String...headers) {
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> st = new HashMap<>();
        st.put(headers[0], "SALARY");
        st.put(headers[1], "$123k");
        data.add(st);

        st = new HashMap<>();
        st.put(headers[0], "PURCHASE");
        st.put(headers[1], "$313k");
        data.add(st);

        return data;
    }

    private void pagination(SimpleDataSource<Integer, Map<String, Object>> dataSource
            , int pageSize
            , int pageCount
            , Consumer<List<Map<String, Object>>> consumer) {
        //Null Check:
        if (consumer == null) {
            consumer.accept(new ArrayList<>());
            return;
        }
        //Validation:
        pageSize = (pageSize <= 0) ? 5 : pageSize;
        int maxCount = (pageSize == dataSource.size()) ? 1 : (dataSource.size() / pageSize) + 1;
        pageCount = (pageCount <= 0) ? maxCount : pageCount;
        //Works:
        int offset = 0; //iDataSource::readAsync is 0-based;
        while (offset <= pageCount) {
            Object[] objs  = dataSource.readSync(offset, pageSize);
            List<Map<String, Object>> items = Stream.of(objs)
                    .map(ob -> (Map<String, Object>) ob)
                    .collect(Collectors.toList());
            consumer.accept(items);
            //Next page:
            offset++;
        }
    }

    private SimpleDataSource<Integer, Map<String, Object>> dataSource() {
        SimpleDataSource<Integer, Map<String, Object>> data = new SimpleDataSource<>();
        AtomicInteger indexCounter = new AtomicInteger(0);
        dummyTransactions().forEach(row -> {
            data.put(indexCounter.getAndIncrement(), row);
        });
        dummyTransactions().forEach(row -> {
            data.put(indexCounter.getAndIncrement(), row);
        });
        dummyTransactions().forEach(row -> {
            data.put(indexCounter.getAndIncrement(), row);
        });
        dummyTransactions().forEach(row -> {
            data.put(indexCounter.getAndIncrement(), row);
        });
        return data;
    }

    private List<Map<String, Object>> dummyTransactions() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(new Row().add("account_ref", "CASH@admin").add("currency", "BDT").add("amount", "-230.0").add("balance", "1219.9").add("transaction_type", "withdrawal").add("transaction_date", "2026-01-14T19:38:20.318").add("transaction_ref", "cc25a914-4a84-4849").keyObjectMap());
        data.add(new Row().add("account_ref", "CASH@admin").add("currency", "BDT").add("amount", "1290.0").add("balance", "1449.9").add("transaction_type", "deposit").add("transaction_date", "2026-01-14T19:37:20.313").add("transaction_ref", "dd54cecd-80a5-4386").keyObjectMap());
        data.add(new Row().add("account_ref", "CASH@admin").add("currency", "BDT").add("amount", "-340.8").add("balance", "879.1").add("transaction_type", "transfer").add("transaction_date", "2026-01-14T19:36:20.312").add("transaction_ref", "ab4c7d73-dc84-433e").keyObjectMap());
        data.add(new Row().add("account_ref", "CASH@admin").add("currency", "BDT").add("amount", "-120.0").add("balance", "759.1").add("transaction_type", "transfer").add("transaction_date", "2026-01-14T19:35:20.317").add("transaction_ref", "daac741d-0ea9-49bc").keyObjectMap());
        data.add(new Row().add("account_ref", "CASH@admin").add("currency", "BDT").add("amount", "-30.1").add("balance", "159.9").add("transaction_type", "transfer").add("transaction_date", "2026-01-14T19:34:20.319").add("transaction_ref", "1248051c-5126-4f80").keyObjectMap());
        return data;
    }
}
