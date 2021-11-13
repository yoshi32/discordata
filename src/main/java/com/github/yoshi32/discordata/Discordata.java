package com.github.yoshi32.discordata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class Discordata {

    public static CompletableFuture<Map<String, Long>> countEvents(File file) {
        CompletableFuture<Map<String, Long>> cf = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            TreeMap<String, Long> map = new TreeMap<>();
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("\"yyyy-MM-dd'T'HH:mm:ss.S'Z'\"");
                long max = 0;
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line == null) break;
                        JSONObject jsonObject = new JSONObject(line);
                        String eventType = jsonObject.getString("event_type");
                        if (eventType.equals("data_request_initiated")) {
                            long current = simpleDateFormat.parse(jsonObject.getString("timestamp")).getTime();
                            if (current > max) max = current;
                        }
                        map.merge(eventType, 1L, (old, ignored) -> old + 1);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        cf.completeExceptionally(e);
                        break;
                    }
                }
                map.put("date_of_request", max);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cf.complete(map);
        });
        return cf;
    }

    public static void writeToFile(File file, Map<String, Long> map) {
        try {
            Files.write(file.toPath(), () -> map.entrySet().stream()
                    .<CharSequence>map(e -> e.getKey() + ": " + e.getValue())
                    .iterator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
