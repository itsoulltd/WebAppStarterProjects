package com.infoworks.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class WordCounterTest {

    private static Logger LOG = LoggerFactory.getLogger(WordCounterTest.class);

    @Test
    void pdfWordCount() {
        WordCounter counter = new WordCounter();
        long count = counter.pdfWordCount("data/Application_Development_Guideline.pdf");
        Assertions.assertEquals(381L, count);
        LOG.info("PDF Word count: " + count);
    }

    @Test
    void xmlWordCountEmpty() {
        WordCounter counter = new WordCounter();
        long countXml = counter.xmlWordCount("data/TestDoc.xml", new String[0]);
        Assertions.assertEquals(0L, countXml);
        LOG.info("XML Word count: " + countXml);
    }

    @Test
    void xmlWordCountBody() {
        WordCounter counter = new WordCounter();
        long countXml = counter.xmlWordCount("data/TestDoc.xml", new String[]{"body"});
        Assertions.assertEquals(49L, countXml);
        LOG.info("XML Body Word count: " + countXml);
        //
        countXml = counter.xmlWordCount("data/TestDoc.xml", new String[]{"body"}, "title");
        Assertions.assertEquals(40L, countXml);
        LOG.info("XML Body except(title) Word count: " + countXml);
    }

    @Test
    void xmlParseBodyContent() {
        WordCounter counter = new WordCounter();

        String body = counter.parseXmlContent("data/TestDoc.xml"
                , new String[]{"body"}, "title");
        LOG.info(body);
    }

    @Test
    void xmlElementRead() {
        XmlElementReader reader = new XmlElementReader();

        Map<String, String> data = reader.read("data/TestDoc.xml"
                , new String[]{"section[@id='conclusion']"}, "title");
        LOG.info(data.toString());
    }
}