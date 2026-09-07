package com.infoworks.services.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.objects.Message;
import com.infoworks.objects.MessageParser;
import com.infoworks.objects.Response;
import com.infoworks.orm.Row;
import com.infoworks.tasks.ExecutableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

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
        /*String filename = data.get("filename").toString();
        String fileSavePath = Path.of(saveDir, filename).toString();
        try (AsyncWriter writer = new StreamWriter(100, fileSavePath)) {
            //Write headers:
            String[] headers = {"AccountName","Currency","Amount","Balance","Type","Date","Ref"};

            Map<Integer, List<String>> headerRow = new HashMap<>();
            headerRow.put(0, Arrays.asList(headers));
            writer.write("data", headerRow);

            //Write in batch:
            String[] colKeys = {"account_ref","currency","amount","balance","transaction_type","transaction_date","transaction_ref"};

            List<Map<String, Object>> transactions = dummyTransactions();
            Map<Integer, List<String>> converted = AsyncWriter.convert(transactions, 1, colKeys); // startIndex need move page by page.

            writer.write("data", converted);
            writer.flush();
            data.put("status", "COMPLETE");
        } catch (Exception e) {
            data.put("status", "FAILED");
            data.put("reason", e.getMessage());
        }*/

        //FIXME: For testing
        try { Thread.sleep(5000); } catch (Exception ignore) {}
        data.put("status", "COMPLETE");
        //...
        return new Response().setStatus(200).setMessage(MessageParser.printJson(data, mapper));
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
