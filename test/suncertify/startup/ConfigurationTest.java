/*
 * ConfigurationTest.java
 *
 * Created on 05-Jun-2005
 */


package suncertify.startup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import junit.framework.TestCase;


/**
 * Unit tests for {@link suncertify.startup.Configuration}.
 * 
 * @author Richard Wardle
 */
public class ConfigurationTest extends TestCase {

    private static Logger logger = Logger.getLogger(ConfigurationTest.class
            .getName());

    /**
     * Creates a new ConfigurationTest.
     * 
     * @param name
     *        Test case name.
     */
    public ConfigurationTest(String name) {
        super(name);
    }

    /**
     * Tests constructor behaviour when called with an invalid properties file
     * path.
     */
    public void testInvalidPropertiesFilePath() {
        try {
            new Configuration(null);
            fail("Properties file path passed to constructor must be "
                    + "non-null.");
        } catch (IllegalArgumentException e) {
            logger.info("Caught expected IllegalArgumentException: "
                    + e.getMessage());
        }

        try {
            new Configuration("");
            fail("Properties file path passed to constructor must not be an "
                    + "empty string.");
        } catch (IllegalArgumentException e) {
            logger.info("Caught expected IllegalArgumentException: "
                    + e.getMessage());
        }
    }

    /**
     * Tests the loading of the configuration from the file.
     * 
     * @throws IOException
     *         If the test properties file cannot be read.
     */
    public void testLoadConfiguration() throws IOException {
        URL url = getResourceUrl("suncertify/startup/suncertify.properties");
        Properties properties = loadTestProperties(url.getPath());
        Configuration configuration = new Configuration(url.getPath());
        assertTrue(configuration.loadConfiguration());

        assertEquals("Server address comparison,", properties
                .getProperty(ConfigurationConstants.ADDRESS_PROPERTY),
                configuration.getServerAddress());
        assertEquals("Server port comparison,", properties
                .getProperty(ConfigurationConstants.PORT_PROPERTY),
                configuration.getServerPort());
        assertEquals("Database file path comparison,", properties
                .getProperty(ConfigurationConstants.PATH_PROPERTY),
                configuration.getDatabaseFilePath());
    }

    /**
     * Tests the loading of the configuration from the file when some properties
     * are missing/empty. The configuration corresponding to the missing/empty
     * properties should retain the default values.
     */
    public void testLoadConfigurationMissingEmptyProperties() {
        URL url = getResourceUrl("suncertify/startup/missingempty.properties");
        Configuration configuration = new Configuration(url.getPath());
        assertTrue(configuration.loadConfiguration());
        assertDefaultConfiguration(configuration);
    }

    /**
     * Tests that the configuration is still set to the default after calling
     * loadProperties if the specified properties file does not exist.
     */
    public void testPropertiesFileDoesntExist() {
        String filename = "doesntexist.properties";
        if (new File(filename).exists()) {
            throw new IllegalStateException("Unexpected file exists at '"
                    + filename + "'; remove file to allow correct test run.");
        }

        Configuration configuration = new Configuration(filename);
        assertFalse(configuration.loadConfiguration());
        assertDefaultConfiguration(configuration);
    }

    /**
     * Tests saving the configuration to a file that already exists.
     * 
     * @throws IOException
     *         If the file cannot be created.
     */
    public void testSaveConfigurationExistingFile() throws IOException {
        String fileName = "test.properties";
        File file = new File(fileName);
        createFile(fileName, file);

        try {
            assertExpectedConfiguration(fileName);
        } finally {
            deleteFile(file);
        }
    }

    /**
     * Tests that the appropriate exception is thrown if the properties file
     * cannot be written to.
     * 
     * @throws IOException
     *         If the test properties file cannot be created.
     */
    public void testSaveConfigurationExistingFileReadOnly() throws IOException {
        String fileName = "test.properties";
        File file = new File(fileName);
        createFile(fileName, file);

        if (!file.setReadOnly()) {
            throw new IllegalStateException(
                    "Unable to set read only on file in working directory "
                            + "called: '" + fileName + "'.");
        }

        try {
            Configuration configuration = new Configuration(fileName);
            configuration.saveConfiguration();
            fail("IOException expected when properties cannot be written to.");
        } catch (IOException e) {
            logger.info("Caught expected IOException: " + e.getMessage());
        } finally {
            deleteFile(file);
        }
    }

    /**
     * Tests saving the configuration to a file that doesn't yet exist.
     * 
     * @throws IOException
     *         If the file cannot be created.
     */
    public void testSaveConfigurationNewFile() throws IOException {
        String fileName = "test.properties";
        try {
            assertExpectedConfiguration(fileName);
        } finally {
            deleteFile(new File(fileName));
        }
    }

    /**
     * Tests that the default configuration is loaded if the constructor is
     * called with a valid properties file path.
     */
    public void testValidPropertiesFilePath() {
        Configuration configuration = new Configuration("valid.properties");
        assertDefaultConfiguration(configuration);
    }

    private void assertDefaultConfiguration(Configuration config) {
        assertEquals("Server address should be set to the default,",
                ConfigurationConstants.DEFAULT_ADDRESS, config
                        .getServerAddress());
        assertEquals("Server port should be set to the default,",
                ConfigurationConstants.DEFAULT_PORT, config.getServerPort());
        assertEquals("Server database file path should be set to the default,",
                ConfigurationConstants.DEFAULT_PATH, config
                        .getDatabaseFilePath());
    }

    private void assertExpectedConfiguration(String fileName)
            throws IOException {
        String expectedAddress = "address";
        String expectedPort = "port";
        String expectedPath = "path";

        Configuration configuration = new Configuration(fileName);
        configuration.setServerAddress(expectedAddress);
        configuration.setServerPort(expectedPort);
        configuration.setDatabaseFilePath(expectedPath);
        configuration.saveConfiguration();

        Properties props = loadTestProperties(fileName);
        assertEquals("Server address comparison,", expectedAddress, props
                .getProperty(ConfigurationConstants.ADDRESS_PROPERTY));
        assertEquals("Server port comparison,", expectedPort, props
                .getProperty(ConfigurationConstants.PORT_PROPERTY));
        assertEquals("Database file path comparison,", expectedPath, props
                .getProperty(ConfigurationConstants.PATH_PROPERTY));
    }

    private void createFile(String fileName, File file) throws IOException {
        if (!file.createNewFile()) {
            throw new IllegalStateException(
                    "Unable to create file in working directory called: '"
                            + fileName + "'.");
        }
    }

    private void deleteFile(File file) {
        if (!file.delete()) {
            System.err.println("Unable to delete test file at: '"
                    + file.getPath() + "'.");
        }
    }

    private URL getResourceUrl(String resourceName) {
        URL url = ClassLoader.getSystemResource(resourceName);
        if (url == null) {
            throw new IllegalStateException("Missing resource at '"
                    + resourceName
                    + "'; resource is required for correct test run.");
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
