package com.infoworks.lab.services.impl;

import com.it.soul.lab.data.base.DataStorage;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service("local")
public class LocalStorageService extends SimpleDataSource<String, InputStream> implements DataStorage, AutoCloseable {

    private String uuid;
    @Override
    public String getUuid() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
        return uuid;
    }

    private Map<String, Boolean> fileSavedStatusMap = new ConcurrentHashMap<>();
    private ExecutorService service = Executors.newSingleThreadExecutor();

    @Override
    public void close() throws Exception {
        fileSavedStatusMap.clear();
        service.shutdown();
    }

    public String[] readKeys(){
        return getInMemoryStorage().keySet().toArray(new String[0]);
    }

    @Override
    public void put(String s, InputStream multipartFile) {
        super.put(s, multipartFile);
        fileSavedStatusMap.put(s, false);
    }

    @Override
    public InputStream remove(String s) {
        fileSavedStatusMap.remove(s);
        return super.remove(s);
    }

    protected synchronized List<String> getUnsavedFiles(){
        final List<String> notSavedYet = fileSavedStatusMap.entrySet()
                .stream()
                .filter(enty -> !enty.getValue())
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        return notSavedYet;
    }

    @Override
    public void save(boolean async) {
        List<String> notSavedYet = getUnsavedFiles();
        notSavedYet.forEach(fileName -> {
            try {
                InputStream file = read(fileName);
                fileSavedStatusMap.put(fileName, saveFile(file, fileName));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    protected boolean saveFile(InputStream file, String name) throws IOException {
        InputStream in = file;
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + name;
        FileOutputStream f = new FileOutputStream(fileLocation);
        int ch = 0;
        while ((ch = in.read()) != -1) {
            f.write(ch);
        }
        f.flush();
        f.close();
        return true;
    }

    @Override
    public boolean retrieve() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }
}
