package com.infoworks.services;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WordCounterTest {

    private static Logger LOG = LoggerFactory.getLogger(WordCounterTest.class);

    @Test
    void pdfWordCount() {
        WordCounter counter = new WordCounter();
        long count = counter.pdfWordCount("data/Application_Development_Guideline.pdf");
        LOG.info("PDF Word count: " + count);
    }

    @Test
    void xmlWordCountAll() {
        WordCounter counter = new WordCounter();
        long countXml = counter.xmlWordCount("data/TestDoc.xml", new String[0]);
        LOG.info("XML All Word count: " + countXml);
    }

    @Test
    void xmlWordCountBody() {
        WordCounter counter = new WordCounter();
        long countXml = counter.xmlWordCount("data/TestDoc.xml", new String[]{"body"});
        LOG.info("XML Body Word count: " + countXml);
        //
        countXml = counter.xmlWordCount("data/TestDoc.xml", new String[]{"body"}, "title");
        LOG.info("XML Body except(title) Word count: " + countXml);
    }
}