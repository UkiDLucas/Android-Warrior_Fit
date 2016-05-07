package com.cyberwalkabout.cyberfit.config.xml;

import com.cyberwalkabout.cyberfit.config.Config;
import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

/**
 * @author Andrii Kovalov
 */
public class XMLConfigReaderTest {
    private static final Logger LOG = LoggerFactory.getLogger(XMLConfigReaderTest.class);
    private static final String CONFIG = "default_config.xml";

    private com.cyberwalkabout.cyberfit.config.xml.XMLConfigReader xmlConfigReader;
    private InputStream in;

    @Before
    public void setUp() throws Exception {
        LOG.debug("setUp");

        URL resource = Resources.getResource(CONFIG);
        assertNotNull(resource);

        in = resource.openStream();

        assertNotNull(in);
        xmlConfigReader = new XMLConfigReader();
    }

    @After
    public void tearDown() throws Exception {
        LOG.debug("tearDown");

        xmlConfigReader = null;
        try {
            in.close();
        } catch (Exception ignore) {
        }
    }

    @Test
    public void testRead() throws Exception {
        LOG.debug("tearRead");

        Config config = xmlConfigReader.readConfig(in);

        assertNotNull(config);

        LOG.debug(config.toString());
    }
}
