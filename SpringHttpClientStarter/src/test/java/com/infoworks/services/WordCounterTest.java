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
    void xmlWordCount() {
        WordCounter counter = new WordCounter();
        long countXml = counter.xmlWordCount("data/TestDoc.xml");
        LOG.info("XML Word count: " + countXml);
    }
}