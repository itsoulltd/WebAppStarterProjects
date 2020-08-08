package com.infoworks.lab.services;

import com.it.soul.lab.data.base.DataStorage;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
public class LocalStorageService extends SimpleDataSource<String, MultipartFile> implements DataStorage, AutoCloseable {

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

    @Override
    public void put(String s, MultipartFile multipartFile) {
        super.put(s, multipartFile);
        fileSavedStatusMap.put(s, false);
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
                MultipartFile file = read(fileName);
                fileSavedStatusMap.put(fileName, saveFile(file));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    protected boolean saveFile(MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + file.getOriginalFilename();
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
