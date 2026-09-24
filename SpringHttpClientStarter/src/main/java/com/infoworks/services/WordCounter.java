package com.infoworks.services;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class WordCounter {

    private static Logger LOG = LoggerFactory.getLogger(WordCounter.class);
    private static final Pattern WORD =
            Pattern.compile("\\b[\\p{L}\\p{N}]+(?:['’-][\\p{L}\\p{N}]+)*\\b");
    private final boolean enableOCR;
    private final XmlElementReader reader;

    /**
     * When a Spring @Component with more-than-one constructors, spring needs to know which one to use for dependency injection.
     * Use @Autowired to mark as default injection candidate.
     * @param enableOCR
     */
    @Autowired
    public WordCounter(@Value("${ocr.enable}") String enableOCR
            , XmlElementReader reader) {
        this.reader = reader;
        this.enableOCR = Boolean.parseBoolean(Optional.ofNullable(enableOCR).orElse("false"));
    }

    public WordCounter() {
        this("false", new XmlElementReader());
    }

    public long pdfWordCount(String filename) throws RuntimeException {
        ClassPathResource resource = new ClassPathResource(filename);
        try (PDDocument document = Loader.loadPDF(resource.getContentAsByteArray())) {
            return pdfWordCount(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long pdfWordCount(InputStream inputStream) throws RuntimeException {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            return pdfWordCount(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long pdfWordCount(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        long wordCount = WORD
                .matcher(text)
                .results()
                .count();
        //OCR:
        if (enableOCR) {
            LOG.info("OCR NOT IMPLEMENTED YET!");
        }
        //LOG.info("Word count: " + wordCount);
        return wordCount;
    }

    public String parsePdfContent(String filename) throws RuntimeException {
        ClassPathResource resource = new ClassPathResource(filename);
        try (PDDocument document = Loader.loadPDF(resource.getContentAsByteArray())) {
            return parsePdfContent(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String parsePdfContent(InputStream inputStream) throws RuntimeException {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            return parsePdfContent(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String parsePdfContent(PDDocument document) throws IOException {
        StringBuilder txtBuilder = new StringBuilder();
        //Read pdf native-text:
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        txtBuilder.append(text);
        //OCR for text:
        if (enableOCR) {
            LOG.info("OCR NOT IMPLEMENTED YET!");
        }
        //LOG.info("Word count: " + wordCount);
        return txtBuilder.toString();
    }

    private List<PDXObject> findPDXObjects(PDDocument document) {
        List<PDXObject> results = new ArrayList<>();
        //Pileup xobjects:
        document.getPages().forEach(page -> {
            PDResources resources = page.getResources();
            resources.getXObjectNames().forEach(name -> {
                try {
                    results.add(resources.getXObject(name));
                } catch (IOException e) {}
            });
        });
        return results;
    }

    private List<PDImageXObject> hasAnyPDImageXObjects(List<PDXObject> objects) {
        List<PDImageXObject> results = new ArrayList<>();
        objects.stream().filter(obj -> obj instanceof PDImageXObject)
                .forEach(obj -> results.add((PDImageXObject) obj));
        return results;
    }

    public long xmlWordCount(String filename, String[] lookupElements, String...skipElements) throws RuntimeException {
        ClassPathResource resource = new ClassPathResource(filename);
        try (InputStream inputStream = resource.getInputStream()) {
            return xmlWordCount(inputStream, lookupElements, skipElements);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process XML", e);
        }
    }

    public long xmlWordCount(InputStream inputStream, String[] lookupElements, String...skipElements) throws RuntimeException {
        long count = 0;
        try {
            String text = parseXmlContent(inputStream, lookupElements, skipElements);
            count += WORD
                    .matcher(text)
                    .results()
                    .count();
        } catch (Exception e) { throw new RuntimeException(e); }
        return count;
    }

    public String parseXmlContent(String filename, String[] lookupElements, String...skipElements) throws RuntimeException {
        ClassPathResource resource = new ClassPathResource(filename);
        try (InputStream inputStream = resource.getInputStream()) {
            return parseXmlContent(inputStream, lookupElements, skipElements);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process XML", e);
        }
    }

    public String parseXmlContent(InputStream inputStream, String[] lookupElements, String...skipElements) throws RuntimeException {
        StringBuilder txtBuilder = new StringBuilder();
        Map<String, String> data = reader.read(inputStream, lookupElements, skipElements);
        data.forEach((element, text) -> {
            if(txtBuilder.isEmpty()) txtBuilder.append(text);
            else txtBuilder.append(" " + text);
        });
        return txtBuilder.toString();
    }
}
