package com.infoworks.services;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

@Service
public class WordCounter {

    private static Logger LOG = LoggerFactory.getLogger(WordCounter.class);
    private static final Pattern WORD =
            Pattern.compile("\\b[\\p{L}\\p{N}]+(?:['’-][\\p{L}\\p{N}]+)*\\b");
    private final XMLInputFactory xmlInputFactory;

    public WordCounter() {
        this.xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities",false);
    }

    public long pdfWordCount(String filename) throws RuntimeException {
        //
        ClassPathResource resource = new ClassPathResource(filename);
        try (PDDocument document = Loader.loadPDF(resource.getContentAsByteArray())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            long wordCount = WORD
                    .matcher(text)
                    .results()
                    .count();
            //LOG.info("Word count: " + wordCount);
            return wordCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long xmlWordCount(String filename, String[] lookupElements, String...skipElements) throws RuntimeException {
        ClassPathResource resource = new ClassPathResource(filename);
        long count = 0;
        try (InputStream inputStream = resource.getInputStream()) {
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
            return count;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process XML", e);
        }
    }
}
