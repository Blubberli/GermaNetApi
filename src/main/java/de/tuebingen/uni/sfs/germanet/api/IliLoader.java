/*
 * Copyright (C) 2012 Department of General and Computational Linguistics,
 * University of Tuebingen
 *
 * This file is part of the Java API to GermaNet.
 *
 * The Java API to GermaNet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java API to GermaNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this API; if not, see <http://www.gnu.org/licenses/>.
 */
package de.tuebingen.uni.sfs.germanet.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Load <code>IliRecords</code> into a specified <code>GermaNet</code> object.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 13.0
 */
class IliLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(IliLoader.class);
    private GermaNet germaNet;
    private String namespace;

    /**
     * Constructs an <code>IliLoader</code> for the specified
     * <code>GermaNet</code> object.
     *
     * @param germaNet the <code>GermaNet</code> object to load the
     *                 <code>IliRecords</code> into
     */
    protected IliLoader(GermaNet germaNet) {
        this.germaNet = germaNet;
    }

    /**
     * Loads <code>IliRecords</code> from the specified file into this
     * <code>IliLoader</code>'s <code>GermaNet</code> object.
     *
     * @param iliFile the file containing <code>IliRecords</code> data
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void loadILI(File iliFile) throws FileNotFoundException, XMLStreamException {
        loadILI(new FileInputStream(iliFile));
    }

    /**
     * Loads <code>IliRecords</code> from the specified stream into this
     * <code>IliLoader</code>'s <code>GermaNet</code> object.
     *
     * @param inputStream the stream containing <code>IliRecords</code> data
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void loadILI(InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(inputStream);
        int event;
        String nodeName;
        int iliCnt = 0;

        LOGGER.info("Loading interLingualIndex_DE-EN.xml...");

        //Parse entire file, looking for ili record start elements
        while (parser.hasNext()) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    namespace = parser.getNamespaceURI();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_ILI_RECORD)) {
                        IliRecord ili = processIliRecord(parser);
                        germaNet.addIliRecord(ili);
                        iliCnt++;
                    }
                    break;
            }
        }
        parser.close();
        LOGGER.info("Done loading {} ILI records.", iliCnt);
    }

    /**
     * Returns the <code>IliRecord</code> for which the start tag was just encountered.
     *
     * @param parser the <code>parser</code> being used on the current file
     * @return a <code>IliRecord</code> representing the data parsed
     * @throws javax.xml.stream.XMLStreamException
     */
    private IliRecord processIliRecord(XMLStreamReader parser) throws XMLStreamException {
        int lexUnitId;
        String ewnRelation;
        String pwnWord;
        String pwn20Id;
        String pwn30Id;
        String pwn20paraphrase = "";
        String source;
        IliRecord curIli;
        List<String> englishSynonyms = new ArrayList<String>();
        boolean done = false;
        int event;
        String nodeName;
        lexUnitId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_LEX_UNIT_ID).substring(1));
        ewnRelation = parser.getAttributeValue(namespace, GermaNet.XML_EWN_RELATION);
        pwnWord = parser.getAttributeValue(namespace, GermaNet.XML_PWN_WORD);
        pwn20Id = parser.getAttributeValue(namespace, GermaNet.XML_PWN20_ID);
        pwn30Id = parser.getAttributeValue(namespace, GermaNet.XML_PWN30_ID);
        pwn20paraphrase = parser.getAttributeValue(namespace, GermaNet.XML_PWN20_PARAPHRASE);

        source = parser.getAttributeValue(namespace, GermaNet.XML_SOURCE);

        // process this lexUnit
        while (parser.hasNext() && !done) {
            event = parser.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_PWN20_SYNONYM)) {
                        englishSynonyms.add(processEnglishSynonyms(parser));
                    }
                case XMLStreamConstants.END_ELEMENT:
                    nodeName = parser.getLocalName();
                    // quit when we reach the end lexUnit tag
                    if (nodeName.equals(GermaNet.XML_ILI_RECORD)) {
                        done = true;
                    }
                    break;
            }
        }

        curIli = new IliRecord(lexUnitId, EwnRel.valueOf(ewnRelation), pwnWord, pwn20Id, pwn30Id, pwn20paraphrase, source);

        for (String synonym : englishSynonyms) {
            curIli.addEnglishSynonym(synonym);
        }

        return curIli;
    }

    /**
     * Returns an English synonym for the currently processed <code>IliRecord</code>
     *
     * @param parser the <code>parser</code> being used on the current file
     * @return <code>String</code> representation of an English synonym
     * @throws javax.xml.stream.XMLStreamException
     */
    private String processEnglishSynonyms(XMLStreamReader parser) throws XMLStreamException {
        String englishSynonym = parser.getElementText();
        return englishSynonym;
    }
}
