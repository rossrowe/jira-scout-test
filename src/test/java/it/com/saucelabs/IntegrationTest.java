package it.com.saucelabs;

import com.saucelabs.ci.sauceconnect.SauceConnectTwoManager;
import com.saucelabs.rest.Credential;
import com.saucelabs.selenium.client.factory.SeleniumFactory;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class IntegrationTest {

    public static final int PORT = 5000;
    protected static final String DEFAULT_SAUCE_DRIVER = "sauce-ondemand:?max-duration=60&os=windows 2008&browser=firefox&browser-version=8.";
    private WebDriver driver;
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();
    private SauceConnectTwoManager sauceTunnelManager;
    public static final String DUMMY_KEY = "TEST";
    private String hostName;


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
        hostName = InetAddress.getLocalHost().getHostName();
        System.out.println("Host name: " + hostName);
        //hostName = "localhost";
        System.setProperty("SELENIUM_DRIVER", DEFAULT_SAUCE_DRIVER);
        System.setProperty("SELENIUM_PORT", "4445");
        System.setProperty("SELENIUM_HOST", "localhost");
        System.setProperty("SELENIUM_STARTING_URL", "http://" + hostName + ":" + PORT + "/jira/secure/AdminSummary.jspa");

        driver = SeleniumFactory.createWebDriver();
//
//        ProfilesIni allProfiles = new ProfilesIni();
//        FirefoxProfile profile = allProfiles.getProfile("selenium");
//        driver = new FirefoxDriver(profile);

        baseUrl = "http://" + hostName + ":" + PORT + "/jira";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void runTests() throws Exception {
        initialize();
        runExportToJira();
        //synchronizeChange();
    }

    public void initialize() throws Exception {

        driver.get(baseUrl + "/secure/AdminSummary.jspa");
        login();
        //create project
        createProject();

        addApplicationLink();
        addConnector();
        displaySettings();
        configureSauceIntegrations();
    }

    private void configureSauceIntegrations() {
        //go to saucelabs.com/account/integrations
        driver.get("https://saucelabs.com/account/integrations");
        sauceLogin();
        driver.findElement(By.name("instance_id")).click();
        driver.findElement(By.name("instance_id")).clear();
        driver.findElement(By.name("instance_id")).sendKeys("1");
        driver.findElement(By.name("object_type_id")).click();
        driver.findElement(By.name("object_type_id")).clear();
        driver.findElement(By.name("object_type_id")).sendKeys("1");
        driver.findElement(By.name("base_url")).click();
        driver.findElement(By.name("base_url")).clear();
        driver.findElement(By.name("base_url")).sendKeys(baseUrl);
        driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
    }

    private void sauceLogin() {
        driver.findElement(By.id("username")).sendKeys("rossco_9_9");
        driver.findElement(By.id("password")).sendKeys("piasal");
        driver.findElement(By.name("submit")).click();
    }

    private void displaySettings() {

        driver.findElement(By.id("admin_summary")).click();
        driver.findElement(By.cssSelector("#admin-summary-section-admin_plugins_menu > #customwareconnectorwebsection44 > li > a")).click();
        driver.findElement(By.cssSelector("#customwareconnectormappingwebitem_tab > strong")).click();
        driver.findElement(By.linkText("Configure")).click();
        driver.findElement(By.cssSelector("h3.toggle-title")).click();
    }

    private void addConnector() {
        driver.findElement(By.id("admin_summary")).click();
        driver.findElement(By.cssSelector("#admin-summary-section-admin_plugins_menu > #customwareconnectorwebsection44 > li > a")).click();
        //accept licence agreement
        driver.findElement(By.name("submit")).click();
        driver.findElement(By.id("connectorId")).clear();
        driver.findElement(By.id("connectorId")).sendKeys("Sauce Labs");
        driver.findElement(By.id("add_submit")).click();
        new Select(driver.findElement(By.id("applink"))).selectByVisibleText("Sauce Labs");
        driver.findElement(By.id("add_submit")).click();

        driver.findElement(By.id("launch_wizard")).click();
        driver.findElement(By.name("setupScreens")).click();
        new Select(driver.findElement(By.name("projects"))).selectByValue("SL");
        driver.findElement(By.id("create_config")).click();
    }

    private void addApplicationLink() {
        driver.findElement(By.cssSelector("#admin-summary-section-admin_plugins_menu > #ual_section > li > a")).click();
        driver.findElement(By.id("add-first-application-link")).click();
        driver.findElement(By.id("application-url")).clear();
        driver.findElement(By.id("application-url")).sendKeys("https://saucelabs.com");
        driver.findElement(By.cssSelector("#add-application-link-dialog > div.dialog-components > div.dialog-button-panel > button.button-panel-button.applinks-next-button")).click();
        driver.findElement(By.name("application-name")).clear();
        driver.findElement(By.name("application-name")).sendKeys("Sauce Labs");
        new Select(driver.findElement(By.id("application-types"))).selectByVisibleText("Sauce Labs");
        driver.findElement(By.cssSelector("#add-application-link-dialog > div.dialog-components > div.dialog-button-panel > button.button-panel-button.wizard-submit")).click();
        driver.findElement(By.linkText("Configure")).click();
        driver.findElement(By.cssSelector("#edit-application-link-dialog > div.dialog-components > ul.dialog-page-menu > li.page-menu-item.selected > button.item-button")).click();
        driver.findElement(By.xpath("//div[@id='edit-application-link-dialog']/div/ul/li[2]/button")).click();
        driver.switchTo().frame("outgoing-auth").switchTo().frame(0);
        driver.findElement(By.id("saucelabs.username")).clear();
        driver.findElement(By.name("saucelabs.username")).sendKeys("rossco_9_9");
        driver.findElement(By.name("saucelabs.token")).clear();
        driver.findElement(By.name("saucelabs.token")).sendKeys("44f0744c-1689-4418-af63-560303cbb37b");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.switchTo().defaultContent();
        driver.findElement(By.linkText("Close")).click();
    }

    private void createProject() {
        driver.findElement(By.id("add_first_project")).click();
        driver.findElement(By.name("name")).clear();
        driver.findElement(By.name("name")).sendKeys("Sauce Labs");
        driver.findElement(By.name("key")).clear();
        driver.findElement(By.name("key")).sendKeys("SL");
        driver.findElement(By.id("add-project-submit")).click();
    }

    private void login() {

        driver.findElement(By.id("login-form-username")).clear();
        driver.findElement(By.id("login-form-username")).sendKeys("admin");
        driver.findElement(By.id("login-form-password")).clear();
        driver.findElement(By.id("login-form-password")).sendKeys("admin");
        driver.findElement(By.id("login-form-submit")).click();
    }

    public void runExportToJira() throws Exception {

        driver.get("https://saucelabs.com/bugs");
        driver.findElement(By.linkText("Export to JIRA")).click();
        driver.switchTo().window("_new");
        new Select(driver.findElement(By.id("projectField"))).selectByVisibleText("Sauce Labs");
        driver.findElement(By.name("create")).click();

        //TODO verify that bug has been created
    }

    public void synchronizeChange() throws Exception {
        //add a new mapping between Jira/Environment and Scount/Browser
        driver.get(baseUrl + "/plugins/servlet/customware/connector/config.action");
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
        driver.get(baseUrl + "/browse/SL-1");
        driver.findElement(By.id("opsbar-operations_more")).click();

        driver.findElement(By.id("connector-issue-links")).click();
        driver.findElement(By.id("connector-pull-changes-1-1")).click();
        driver.findElement(By.id("key-val")).click();

        //TODO validate that the Browser value has been copied into Jira
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

}
