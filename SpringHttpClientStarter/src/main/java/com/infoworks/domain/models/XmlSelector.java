package com.infoworks.domain.models;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record XmlSelector(String elementName, Map<String, String> attributes) {

    /**
     * {} means optional, <> means variable
     * Selector pattern: <tag-name>[@<{xml:}attribute-name>{='value'}]
     * e.g. article[@xml:lang], article[@xml:lang='de'], article[@article-type] etc
     */
    private static final Pattern SELECTOR_PATTERN =
            Pattern.compile("^([\\w:.-]+)((?:\\[@[\\w:.-]+(?:\\s*=\\s*(['\"])(.*?)\\3)?\\])*)$");

    private static final Pattern ATTRIBUTE_PATTERN =
            Pattern.compile("\\[@([\\w:.-]+)(?:\\s*=\\s*(['\"])(.*?)\\2)?\\]");


    /**
     * Following only accept <tag-name>[@<{xml:}attribute-name>='value'] where value is mandatory.
     */
    /*private static final Pattern SELECTOR_PATTERN =
            Pattern.compile("^([\\w:.-]+)((?:\\[@[\\w:.-]+\\s*=\\s*(['\"])(.*?)\\3\\])*)$");*/

    /*private static final Pattern ATTRIBUTE_PATTERN =
            Pattern.compile("\\[@([\\w:.-]+)\\s*=\\s*(['\"])(.*?)\\2\\]");*/

    public static XmlSelector parse(String selector) {
        Matcher matcher = SELECTOR_PATTERN.matcher(selector.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid XML selector: " + selector);
        }

        String elementName = matcher.group(1);
        String attributesPart = matcher.group(2);

        Map<String, String> attributes = new LinkedHashMap<>();
        Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(attributesPart);

        while (attributeMatcher.find()) {
            String name = attributeMatcher.group(1);
            String value = null;
            try { value = attributeMatcher.group(3); } catch (Exception ignore) {}
            attributes.put(name, value);
        }
        return new XmlSelector(elementName, attributes);
    }

    public boolean matchesStart(XMLStreamReader reader) {

        if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
            return false;
        }

        if (!elementName.equals(reader.getLocalName())) {
            return false;
        }

        for (var entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String expectedValue = entry.getValue();
            if (expectedValue != null) {
                String actualValue = attributeValue(reader, attributeName);
                if (!Objects.equals(expectedValue, actualValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean matchesEnd(XMLStreamReader reader) {
        return reader.getEventType() == XMLStreamConstants.END_ELEMENT
                && elementName.equals(reader.getLocalName());
    }

    public String attributeValue(XMLStreamReader reader, String attributeName) {

        if ("xml:lang".equals(attributeName)) {
            return reader.getAttributeValue(
                    XMLConstants.XML_NS_URI,
                    "lang"
            );
        }

        // For normal, non-namespaced attributes
        return reader.getAttributeValue(
                null,
                attributeName
        );
    }

    public boolean isSearchingForAttributeValue() {
        //Has any attribute with null value, means we are looking for its value.
        return this.attributes.values().stream().anyMatch(Objects::isNull);
    }

    public String elementValue(XMLStreamReader reader) {
        try {
            return reader.getElementText();
        } catch (XMLStreamException e) {
            return null;
        }
    }
}

