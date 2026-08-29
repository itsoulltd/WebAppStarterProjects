package com.infoworks.services;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class WordCounter {

    private static Logger LOG = LoggerFactory.getLogger(WordCounter.class);
    private static final Pattern WORD =
            Pattern.compile("\\b[\\p{L}\\p{N}]+(?:['’-][\\p{L}\\p{N}]+)*\\b");
    private final XMLInputFactory xmlInputFactory;
    private final boolean enableOCR;

    public WordCounter(@Value("${ocr.enable}") String enableOCR) {
        this.xmlInputFactory = XMLInputFactory.newFactory();
        this.xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        this.xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities",false);
        this.enableOCR = Boolean.parseBoolean(Optional.ofNullable(enableOCR).orElse("false"));
    }

    public WordCounter() {
        this("false");
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
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(inputStream);
            var skips = Arrays.asList(skipElements);
            var skipDepth = 0;
            var lookups = Arrays.asList(lookupElements);
            var isInLookupScop = lookups.isEmpty(); //if lookups is empty then all words get counted.
            try {
                while (reader.hasNext()) {
                    int event = reader.next();
                    //
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        if (lookups.contains(reader.getLocalName())) isInLookupScop = true;
                        if (skips.contains(reader.getLocalName())) skipDepth++;
                        continue;
                    } else if (event == XMLStreamConstants.END_ELEMENT) {
                        if (lookups.contains(reader.getLocalName())) isInLookupScop = false;
                        if (skips.contains(reader.getLocalName())) skipDepth--;
                        continue;
                    }
                    //
                    if (isInLookupScop && skipDepth == 0) {
                        if (event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA) {
                            String text = reader.getText();
                            count += WORD
                                    .matcher(text)
                                    .results()
                                    .count();
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return count;
    }
}
