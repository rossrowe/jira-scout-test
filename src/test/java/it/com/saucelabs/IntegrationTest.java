package it.com.saucelabs;

import com.saucelabs.ci.sauceconnect.SauceConnectTwoManager;
import com.saucelabs.rest.Credential;
import com.saucelabs.selenium.client.factory.SeleniumFactory;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class IntegrationTest {

    public static final int PORT = 5000;
    protected static final String DEFAULT_SAUCE_DRIVER = "sauce-ondemand:?max-duration=60&os=windows 2008&browser=firefox&browser-version=4.";
    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
    private static final String firefox = "E:/Program Files/Firefox/firefox.exe";
    private SauceConnectTwoManager sauceTunnelManager;
    public static final String DUMMY_KEY = "TEST";


    @Before
    public void setUp() throws Exception {

        File sauceSettings = new File(new File(System.getProperty("user.home")), ".sauce-ondemand");
        if (!sauceSettings.exists()) {
            String userName = System.getProperty("sauce.user");
            String accessKey = System.getProperty("access.key");
            if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(accessKey)) {
                Credential credential = new Credential(userName, accessKey);
                credential.saveTo(sauceSettings);
            }
        }
        Credential c = new Credential();
        sauceTunnelManager = new SauceConnectTwoManager();
        Process sauceConnect = (Process) sauceTunnelManager.openConnection(c.getUsername(), c.getKey());
        sauceTunnelManager.addTunnelToMap(DUMMY_KEY, sauceConnect);
        System.setProperty("SELENIUM_DRIVER", DEFAULT_SAUCE_DRIVER);
        System.setProperty("SELENIUM_PORT", "4445");
        System.setProperty("SELENIUM_HOST", "localhost");
//        System.setProperty("SELENIUM_STARTING_URL", "http://localhost:" + PORT + "/jira/secure/AdminSummary.jspa");
        System.setProperty("SELENIUM_STARTING_URL", "http://www.google.com");

        driver = SeleniumFactory.createWebDriver();
        //driver = new FirefoxDriver();
        baseUrl = "http://localhost:" + PORT + "/jira";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testIntegration() throws Exception {

        //go to administration
        driver.get(baseUrl + "/secure/AdminSummary.jspa");
        //login
        driver.findElement(By.id("login-form-username")).clear();
        driver.findElement(By.id("login-form-username")).sendKeys("admin");
        driver.findElement(By.id("login-form-password")).clear();
        driver.findElement(By.id("login-form-password")).sendKeys("admin");
        driver.findElement(By.id("login-form-submit")).click();

        //create project
        driver.findElement(By.id("add_first_project")).click();
        driver.findElement(By.name("name")).clear();
        driver.findElement(By.name("name")).sendKeys("Sauce Labs");
        driver.findElement(By.name("key")).clear();
        driver.findElement(By.name("key")).sendKeys("SL");
        driver.findElement(By.id("add-project-submit")).click();

        //add a new connector
//		driver.findElement(By.cssSelector("#admin_plugins_menu_drop > span")).click();
//		driver.findElement(By.xpath("//a[@id='upm_section']")).click();
        //TODO validate that plugins are installed

        driver.get(baseUrl + "/plugins/servlet/customware/connector/applinks/config.action?type=saucelabs");
        //check to see if license agreement appears, if so, click it
        if (By.name("submit") != null) {
            driver.findElement(By.name("submit")).click();
            driver.get(baseUrl + "/plugins/servlet/customware/connector/applinks/config.action?type=saucelabs");
        }

        driver.findElement(By.name("name")).clear();
        driver.findElement(By.name("name")).sendKeys("Sauce Labs");
        driver.findElement(By.name("rpcurl")).clear();
        driver.findElement(By.name("rpcurl")).sendKeys("https://saucelabs.com");
        driver.findElement(By.name("displayurl")).clear();
        driver.findElement(By.name("displayurl")).sendKeys("https://saucelabs.com");
        driver.findElement(By.name("Add")).click();

        driver.findElement(By.id("configauth-751dd8c8-be04-3477-b92a-5130cd39c6dd")).click();
        driver.findElement(By.id("saucelabs.username")).clear();
        driver.findElement(By.id("saucelabs.username")).sendKeys("rossco_9_9");
        driver.findElement(By.id("saucelabs.token")).clear();
        driver.findElement(By.id("saucelabs.token")).sendKeys("44f0744c-1689-4418-af63-560303cbb37b");
        driver.findElement(By.cssSelector("input.button")).click();

        driver.get(baseUrl + "/secure/AdminSummary.jspa");

        //add the connector
        driver.findElement(By.cssSelector("#admin_plugins_menu_drop > span")).click();
        driver.findElement(By.id("customwareconnectorconnectionwebitem")).click();
        driver.findElement(By.id("connectorId")).clear();
        driver.findElement(By.id("connectorId")).sendKeys("Sauce Labs");
        driver.findElement(By.id("add_submit")).click();

        new Select(driver.findElement(By.id("applink"))).selectByVisibleText("Sauce Labs");
        driver.findElement(By.id("add_submit")).click();
        //launch the configuration wizard
        driver.findElement(By.id("launch_wizard")).click();
        driver.findElement(By.name("setupScreens")).click();
        new Select(driver.findElement(By.name("projects"))).selectByValue("SL");
        driver.findElement(By.id("create_config")).click();
    }

    @Test
    @Ignore
    public void exportToJira() throws Exception {
        //TODO Go to Scout - My Bugs and export to Jira
        driver.findElement(By.linkText("Export to JIRA")).click();
        new Select(driver.findElement(By.id("projectField"))).selectByVisibleText("Sauce Labs");
        driver.findElement(By.name("create")).click();

        //Verify that bug has been created
    }

    @Test
    @Ignore
    public void synchronizeChange() throws Exception {
        //add a new mapping between Jira/Environment and Scount/Browser
        driver.get(baseUrl + "/jira/plugins/servlet/customware/connector/config.action");
        driver.findElement(By.cssSelector("#customwareconnectormappingwebitem_tab > strong")).click();
        driver.findElement(By.id("mappingedit1")).click();
        driver.findElement(By.cssSelector("#customwareconnectormappingschemewebitem_tab > strong")).click();
        driver.findElement(By.id("mappingschemeconfigure1")).click();
        driver.findElement(By.linkText("Sauce Labs 1 Mapping")).click();
        new Select(driver.findElement(By.name("mapJiraField"))).selectByVisibleText("Environment");
        new Select(driver.findElement(By.name("mapRemoteField"))).selectByVisibleText("Browser");
        driver.findElement(By.name("Add")).click();
        driver.findElement(By.id("leave_admin")).click();

        //Trigger a synchronization
        driver.findElement(By.cssSelector("#browse_link_drop > span")).click();
        driver.findElement(By.id("admin_main_proj_link_lnk")).click();
        driver.findElement(By.linkText("SL-1")).click();
        driver.findElement(By.cssSelector("span.icon.drop-menu")).click();
        driver.findElement(By.id("connector-issue-links")).click();
        driver.findElement(By.id("connector-pull-changes-1-1")).click();
        driver.findElement(By.id("key-val")).click();

        //validate that the Browser value has been copied into Jira
    }

    @After
    public void tearDown() throws Exception {
        try {
            if (driver != null) {
                driver.quit();
            }

            String verificationErrorString = verificationErrors.toString();
            if (!"".equals(verificationErrorString)) {
                fail(verificationErrorString);
            }
        } finally {
            sauceTunnelManager.closeTunnelsForPlan(DUMMY_KEY);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
