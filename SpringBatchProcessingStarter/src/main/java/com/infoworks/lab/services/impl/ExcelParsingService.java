package com.infoworks.lab.services.impl;

import com.infoworks.lab.services.definition.ContentWriter;
import com.infoworks.lab.services.definition.ReadingService;
import com.infoworks.lab.services.definition.WritingService;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ExcelParsingService implements ReadingService, WritingService {

    private static Logger LOG = Logger.getLogger(ExcelParsingService.class.getSimpleName());

    public void readAsync(InputStream inputStream
            , Integer bufferSize
            , Integer sheetAt
            , Integer beginIndex
            , Integer endIndex
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(pageSize)
                .bufferSize(bufferSize)
                .open(inputStream);
        configureWorkbook(workbook);
        readBuffered(workbook, sheetAt, beginIndex, endIndex, pageSize, consumer);
        workbook.close();
    }

    public void readAsync(File file
            , Integer bufferSize
            , Integer sheetAt
            , Integer beginIndex
            , Integer endIndex
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(pageSize)
                .bufferSize(bufferSize)
                .open(file);
        configureWorkbook(workbook);
        readBuffered(workbook, sheetAt, beginIndex, endIndex, pageSize, consumer);
        workbook.close();
    }

    /**
     *
     * @param workbook
     * @param sheetAt
     * @param beginIndex the beginning index, inclusive.
     * @param endIndex the ending index, exclusive.
     * @param pageSize
     * @param consumer
     * @throws IOException
     */
    private void readBuffered(Workbook workbook
            , Integer sheetAt
            , Integer beginIndex
            , Integer endIndex
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Sheet sheet = workbook.getSheetAt(sheetAt);
        int maxCount = sheet.getLastRowNum() + 1;
        pageSize = (pageSize > maxCount) ? maxCount : pageSize;
        if (endIndex <= 0 || endIndex == Integer.MAX_VALUE) endIndex = maxCount;
        //
        int idx = -1;
        Map<Integer, List<String>> data = new HashMap<>();
        for (Row row : sheet){
            if (++idx < beginIndex) {continue;}
            if (idx >= endIndex) {break;}
            //
            data.put(idx, new ArrayList<>());
            for (Cell cell : row){
                addInto(data, idx, cell);
            }
            if (consumer != null && data.size() == pageSize ){
                Map xData = new HashMap(data);
                data.clear();
                consumer.accept(xData);
            }
        }
        //left-over
        if (consumer != null && data.size() > 0 ){
            Map xData = new HashMap(data);
            data.clear();
            consumer.accept(xData);
        }
    }

    public void read(InputStream inputStream
            , Integer sheetAt
            , Integer startAt
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Workbook workbook = WorkbookFactory.create(inputStream);
        configureWorkbook(workbook);
        readAsync(workbook, sheetAt, startAt, pageSize, consumer);
        workbook.close();
    }

    public void read(File file
            , Integer sheetAt
            , Integer startAt
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Workbook workbook = WorkbookFactory.create(file);
        configureWorkbook(workbook);
        readAsync(workbook, sheetAt, startAt, pageSize, consumer);
        workbook.close();
    }

    private void readAsync(Workbook workbook
            , Integer sheetAt
            , Integer startAt
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Sheet sheet = workbook.getSheetAt(sheetAt);
        int maxCount = sheet.getLastRowNum() + 1;
        int loopCount = (pageSize == maxCount) ? 1 : (maxCount / pageSize) + 1;
        pageSize = (pageSize > maxCount) ? maxCount : pageSize;
        int index = 0;
        int start = (startAt < 0 || startAt >= maxCount) ? 0 : startAt;
        while (index < loopCount){
            int end = start + pageSize;
            if (end >= maxCount) end = maxCount;
            Map res = parseContent(workbook, sheetAt, start, end);
            if (consumer != null && res.size() > 0){
                consumer.accept(res);
            }
            //
            start += pageSize;
            index++;
        }
    }

    public Map<Integer, List<String>> read(InputStream inputStream, Integer sheetAt, Integer start, Integer end) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        configureWorkbook(workbook);
        Map res = parseContent(workbook, sheetAt, start, end);
        workbook.close();
        return res;
    }

    public Map<Integer, List<String>> readXls(InputStream inputStream, Integer sheetAt, Integer start, Integer end) throws IOException {
        Workbook workbook = new HSSFWorkbook(inputStream);
        configureWorkbook(workbook);
        Map res = parseContent(workbook, sheetAt, start, end);
        workbook.close();
        return res;
    }

    public Map<Integer, List<String>> read(File file, Integer sheetAt, Integer start, Integer end) throws IOException {
        Workbook workbook = WorkbookFactory.create(file);
        configureWorkbook(workbook);
        Map res = parseContent(workbook, sheetAt, start, end);
        workbook.close();
        return res;
    }

    private void configureWorkbook(Workbook workbook) {
        if (workbook != null){
            try {
                //Add All kind of setting for workbook:
                workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            }catch (UnsupportedOperationException e){
                LOG.log(Level.WARNING, e.getMessage());
            }catch (Exception e){
                LOG.log(Level.WARNING, e.getMessage());
            }
        }
    }

    private Map<Integer, List<String>> parseContent(Workbook workbook, Integer sheetAt, Integer start, Integer end) throws IOException {
        //DoTheMath:
        Sheet sheet = workbook.getSheetAt(sheetAt);
        Map<Integer, List<String>> data = new HashMap<>();
        //
        if (end <= 0 || end == Integer.MAX_VALUE){
            end = sheet.getLastRowNum() + 1;
        }
        int idx = (start < 0) ? 0 : start;
        while (idx < end) {
            data.put(idx, new ArrayList<>());
            for (Cell cell : sheet.getRow(idx)) {
                addInto(data, idx, cell);
            }
            idx++;
        }
        return data;
    }

    private void addInto(Map<Integer, List<String>> data, int idx, Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                data.get(idx).add(cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    data.get(idx).add(cell.getDateCellValue() + "");
                } else {
                    data.get(idx).add(NumberToTextConverter.toText(cell.getNumericCellValue()));
                }
                break;
            case BOOLEAN:
                data.get(idx).add(cell.getBooleanCellValue() + "");
                break;
            case FORMULA:
                data.get(idx).add(cell.getStringCellValue() + "");
                break;
            default:
                data.get(idx).add(" ");
        }
    }

    public void write(boolean xssf, OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception {
        AsyncWriter writer = new AsyncWriter(xssf, outputStream);
        writer.write(sheetName, data, false);
        writer.close();
    }

    public void writeAsStream(OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception {
        AsyncWriter writer = new AsyncStreamWriter(100, outputStream);
        writer.write(sheetName, data, false);
        writer.close();
    }

    public AsyncWriter createWriter(boolean xssf, String outFileName, boolean replace) {
        try {
            if(outFileName == null || outFileName.isEmpty()) return null;
            if (replace) removeIfExist(outFileName);
            return new AsyncWriter(xssf, outFileName);
        } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage());}
        return null;
    }

    public AsyncWriter createAsyncWriter(int rowSize, String outFileName, boolean replace) {
        try {
            if(outFileName == null || outFileName.isEmpty()) return null;
            if (replace) removeIfExist(outFileName);
            return new AsyncStreamWriter(rowSize, outFileName);
        } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage());}
        return null;
    }

    private boolean removeIfExist(String outFileName){
        try {
            File outFile = new File(outFileName);
            if (outFile.exists() && outFile.isFile()){
                return outFile.delete();
            }
        } catch (Exception e) {LOG.log(Level.WARNING, e.getMessage());}
        return false;
    }

    //AsyncWriter Start:
    public static class AsyncWriter implements ContentWriter {

        protected Workbook workbook;
        protected OutputStream outfile;

        public AsyncWriter() {}

        public AsyncWriter(boolean xssf, OutputStream outputStream) throws IOException {
            this.workbook = WorkbookFactory.create(xssf);
            this.outfile = outputStream;
        }

        public AsyncWriter(boolean xssf, String fileNameToWrite) throws IOException {
            this(xssf, new FileOutputStream(fileNameToWrite, true));
        }

        @Override
        public void close() throws Exception {
            if (workbook != null) {
                workbook.write(outfile);
                if (outfile != null) {
                    outfile.close();
                    outfile = null;
                }
                if (workbook instanceof SXSSFWorkbook){
                    ((SXSSFWorkbook) workbook).dispose();
                }
                workbook.close();
                workbook = null;
            }
        }

        public void write(String sheetName, Map<Integer, List<String>> data, boolean skipZeroIndex) {
            //DoTheMath:
            Sheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) sheet = workbook.createSheet(sheetName);
            int rowIndex = 0;
            for (Map.Entry<Integer, List<String>> entry : data.entrySet()){
                Row row = sheet.createRow((skipZeroIndex) ? entry.getKey() : rowIndex);
                int cellIndex = 0;
                for (String cellVal : entry.getValue()) {
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(cellVal);
                    if(sheet instanceof XSSFSheet)
                        sheet.autoSizeColumn(cellIndex);
                    cellIndex++;
                }
                rowIndex++;
            }
        }

    }
    //AsyncWriter Done:

    public static class AsyncStreamWriter extends AsyncWriter{

        public AsyncStreamWriter(int rowSize, OutputStream outputStream){
            if (rowSize <= 0) rowSize = 100;
            this.workbook = new SXSSFWorkbook(rowSize);
            this.outfile = outputStream;
        }

        public AsyncStreamWriter(int rowSize, String fileNameToWrite) throws IOException {
            this(rowSize, new FileOutputStream(fileNameToWrite, true));
        }

    }

}
