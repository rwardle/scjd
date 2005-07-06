/*
 * ConfigurationTest.java
 *
 * Created on 05-Jun-2005
 */


package suncertify;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import junit.framework.TestCase;


/**
 * Unit tests for {@link suncertify.Configuration}.
 *
 * @author Richard Wardle
 */
public final class ConfigurationTest extends TestCase {

    private static Logger logger = Logger.getLogger(ConfigurationTest.class
            .getName());

    /**
     * Creates a new instance of <code>ConfigurationTest</code>.
     *
     * @param name The test case name.
     */
    public ConfigurationTest(String name) {
        super(name);
    }

    /**
     * Tests {@link Configuration#Configuration(String)} with a
     * <code>null</code> properties file path.
     */
    public void testNullPropertiesFilePath() {
        try {
            new Configuration(null);
            fail("IllegalArgumentException expected when properties file path "
                    + "is null");
        } catch (IllegalArgumentException e) {
            ConfigurationTest.logger.info("Caught expected "
                    + "IllegalArgumentException: " + e.getMessage());
        }
    }

    /**
     * Tests {@link Configuration#Configuration(String)} with an empty string
     * properties file path.
     */
    public void testEmptyStringPropertiesFilePath() {
        try {
            new Configuration("");
            fail("IllegalArgumentException expected when properties file path "
                    + "is an empty string");
        } catch (IllegalArgumentException e) {
            ConfigurationTest.logger.info("Caught expected "
                    + "IllegalArgumentException: " + e.getMessage());
        }
    }

    /**
     * Tests {@link Configuration#Configuration(String)} with a valid
     * properties file path string.
     */
    public void testValidPropertiesFilePath() {
        Configuration configuration = new Configuration("valid.properties");
        assertDefaultConfiguration(configuration);
    }

    /**
     * Tests {@link Configuration#loadConfiguration} with a properties file
     * containing all keys and values.
     *
     * @throws IOException If the properties file cannot be read.
     */
    public void testLoadConfiguration() throws IOException {
        URL url = getResourceUrl("suncertify/suncertify.properties");
        Properties properties = loadTestProperties(url.getPath());
        Configuration configuration = new Configuration(url.getPath());
        assertTrue(configuration.loadConfiguration());

        assertEquals("Server address comparison,",
                properties.getProperty(ApplicationConstants
                        .ADDRESS_PROPERTY),
                configuration.getServerAddress());
        assertEquals("Server port comparison,",
                properties.getProperty(ApplicationConstants
                        .PORT_PROPERTY),
                configuration.getServerPort());
        assertEquals("Database file path comparison,",
                properties.getProperty(ApplicationConstants
                        .PATH_PROPERTY),
                configuration.getDatabaseFilePath());
    }

    /**
     * Tests {@link Configuration#loadConfiguration} with a properties file
     * in which all properties are either missing or empty.
     */
    public void testLoadConfigurationMissingEmptyProperties() {
        URL url = getResourceUrl("suncertify/missingempty.properties");
        Configuration configuration = new Configuration(url.getPath());
        assertTrue(configuration.loadConfiguration());
        assertDefaultConfiguration(configuration);
    }

    /**
     * Tests {@link Configuration#loadConfiguration} with a properties file
     * that doesn't exist.
     */
    public void testPropertiesFileDoesntExist() {
        String filename = "doesntexist.properties";
        if (new File(filename).exists()) {
            throw new IllegalStateException("Unexpected file exists at '"
                    + filename + "'; remove file to allow correct test run");
        }

        Configuration configuration = new Configuration(filename);
        assertFalse(configuration.loadConfiguration());
        assertDefaultConfiguration(configuration);
    }

    /**
     * Tests {@link Configuration#saveConfiguration} with a properties file
     * that already exists.
     *
     * @throws IOException If the test file cannot be created.
     */
    public void testSaveConfigurationExistingFile() throws IOException {
        String fileName = "test.properties";
        File file = new File(fileName);
        createFile(fileName, file);

        try {
            assertExpectedConfigurationAfterSave(fileName);
        } finally {
            deleteFile(file);
        }
    }

    /**
     * Tests {@link Configuration#saveConfiguration} with a properties file
     * that already exists and is readonly.
     *
     * @throws IOException If the test file cannot be created.
     */
    public void testSaveConfigurationExistingFileReadOnly() throws IOException {
        String fileName = "test.properties";
        File file = new File(fileName);
        createFile(fileName, file);

        if (!file.setReadOnly()) {
            throw new IllegalStateException("Unable to set read-only on file "
                    + "in working directory called: '" + fileName + "'");
        }

        try {
            Configuration configuration = new Configuration(fileName);
            configuration.saveConfiguration();
            fail("IOException expected when properties file is read only");
        } catch (IOException e) {
            ConfigurationTest.logger.info("Caught expected IOException: "
                    + e.getMessage());
        } finally {
            deleteFile(file);
        }
    }

    /**
     * Tests {@link Configuration#saveConfiguration} with a file that doesn't
     * yet exist.
     *
     * @throws IOException If the file cannot be created.
     */
    public void testSaveConfigurationNewFile() throws IOException {
        String fileName = "test.properties";
        try {
            assertExpectedConfigurationAfterSave(fileName);
        } finally {
            deleteFile(new File(fileName));
        }
    }

    private void assertDefaultConfiguration(Configuration config) {
        assertEquals("Server address comparison,",
                ApplicationConstants.DEFAULT_ADDRESS,
                config.getServerAddress());
        assertEquals("Server port comparison,",
                ApplicationConstants.DEFAULT_PORT, config.getServerPort());
        assertEquals("Database file path comparison,",
                ApplicationConstants.DEFAULT_PATH,
                config.getDatabaseFilePath());
    }

    private void assertExpectedConfigurationAfterSave(String fileName) throws
            IOException {
        String expectedAddress = "address";
        String expectedPort = "port";
        String expectedPath = "path";

        Configuration configuration = new Configuration(fileName);
        configuration.setServerAddress(expectedAddress);
        configuration.setServerPort(expectedPort);
        configuration.setDatabaseFilePath(expectedPath);
        configuration.saveConfiguration();

        Properties props = loadTestProperties(fileName);
        assertEquals("Server address comparison,", expectedAddress,
                props.getProperty(ApplicationConstants.ADDRESS_PROPERTY));
        assertEquals("Server port comparison,", expectedPort,
                props.getProperty(ApplicationConstants.PORT_PROPERTY));
        assertEquals("Database file path comparison,", expectedPath,
                props.getProperty(ApplicationConstants.PATH_PROPERTY));
    }

    private void createFile(String fileName, File file) throws IOException {
        if (!file.createNewFile()) {
            throw new IllegalStateException(
                    "Unable to create file in working directory called: '"
                            + fileName + "'");
        }
    }

    private void deleteFile(File file) {
        if (!file.delete()) {
            System.err.println("Unable to delete test file at: '"
                    + file.getPath() + "'");
        }
    }

    private URL getResourceUrl(String resourceName) {
        URL url = ClassLoader.getSystemResource(resourceName);
        if (url == null) {
            throw new IllegalStateException("Missing resource at '"
                    + resourceName
                    + "'; resource is required for correct test run");
        }
        return url;
    }

    private Properties loadTestProperties(String path) throws IOException {
        Properties properties = new Properties();
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(path));
            properties.load(in);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return properties;
    }
}
