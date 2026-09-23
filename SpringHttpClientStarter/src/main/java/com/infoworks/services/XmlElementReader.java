package com.infoworks.services;

import com.infoworks.domain.models.XmlSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class XmlElementReader {

    private static Logger LOG = LoggerFactory.getLogger(XmlElementReader.class);
    private final XMLInputFactory xmlInputFactory;

    public XmlElementReader() {
        this.xmlInputFactory = XMLInputFactory.newFactory();
        this.xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        this.xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities",false);
    }

    public Map<String, String> read(String filename, String[] lookupElements, String...skipElements)
            throws RuntimeException {
        ClassPathResource resource = new ClassPathResource(filename);
        try (InputStream inputStream = resource.getInputStream()) {
            return read(inputStream, lookupElements, skipElements);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process XML", e);
        }
    }

    public Map<String, String> read(InputStream inputStream, String[] lookupElements, String...skipElements) {
        StringBuilder txtBuilder = new StringBuilder();
        Map<String, String> result = new HashMap<>();
        try {
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(inputStream);
            var skips = Arrays.asList(skipElements);
            var lookups = Arrays.asList(lookupElements);
            var lookupSelectors = lookups.stream().map(XmlSelector::parse).toList();
            var skipDepth = 0;
            var isInLookupScop = lookups.isEmpty(); //if lookups is empty then all words get counted.
            try {
                while (reader.hasNext()) {
                    int event = reader.next();
                    //
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        List<XmlSelector> founds = lookupSelectors.stream().filter(s -> s.matchesStart(reader)).collect(Collectors.toList());
                        if (!founds.isEmpty()) {
                            for (XmlSelector selector : founds) {
                                if (result.containsKey(selector.elementName())) continue;
                                if (selector.isSearchingForAttributeValue()) {
                                    selector.attributes().forEach((key, val) -> {
                                        if (val == null) result.put(key, selector.attributeValue(reader, key));
                                    });
                                } else {
                                    isInLookupScop = true;
                                }
                            }
                        }
                        if (skips.contains(reader.getLocalName())) skipDepth++;
                        continue;
                    } else if (event == XMLStreamConstants.END_ELEMENT) {
                        Optional<XmlSelector> found = lookupSelectors.stream().filter(s -> s.matchesEnd(reader)).findFirst();
                        if (found.isPresent() && isInLookupScop) {
                            isInLookupScop = false;
                            String elementText = txtBuilder.toString();
                            result.put(found.get().elementName(), elementText);
                            txtBuilder = new StringBuilder(); //Prepare for next element.
                        }
                        if (skips.contains(reader.getLocalName())) skipDepth--;
                        continue;
                    }
                    //
                    if (isInLookupScop && skipDepth == 0) {
                        if (event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA) {
                            String text = reader.getText();
                            if(txtBuilder.isEmpty()) txtBuilder.append(text);
                            else txtBuilder.append(" " + text);
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return result;
    }
}
