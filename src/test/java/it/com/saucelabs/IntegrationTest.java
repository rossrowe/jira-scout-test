package it.com.saucelabs;

import com.saucelabs.ci.sauceconnect.SauceConnectTwoManager;
import com.saucelabs.rest.Credential;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
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
//        Credential c = new Credential();
//        sauceTunnelManager = new SauceConnectTwoManager();
//        Process sauceConnect = (Process) sauceTunnelManager.openConnection(c.getUsername(), c.getKey());
//        sauceTunnelManager.addTunnelToMap(DUMMY_KEY, sauceConnect);
//        hostName = getHostName();
        hostName = "localhost";
        System.setProperty("SELENIUM_DRIVER", DEFAULT_SAUCE_DRIVER);
        System.setProperty("SELENIUM_PORT", "4445");
        System.setProperty("SELENIUM_HOST", "localhost");
        System.setProperty("SELENIUM_STARTING_URL", "http://" + hostName + ":" + PORT + "/jira/secure/AdminSummary.jspa");

//        driver = SeleniumFactory.createWebDriver();
//
        ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile profile = allProfiles.getProfile("selenium");
        driver = new FirefoxDriver(profile);

        baseUrl = "http://" + hostName + ":" + PORT + "/jira";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    private String getHostName() {

        try {
            // Replace eth0 with your interface name
            NetworkInterface i = null;

            i = NetworkInterface.getByName("eth0");


            if (i != null) {

                Enumeration<InetAddress> iplist = i.getInetAddresses();

                InetAddress addr = null;

                while (iplist.hasMoreElements()) {
                    InetAddress ad = iplist.nextElement();
                    byte bs[] = ad.getAddress();
                    if (bs.length == 4 && bs[0] != 127) {
                        addr = ad;
                        // You could also display the host name here, to
                        // see the whole list, and remove the break.
                        break;
                    }
                }

                if (addr != null) {
                    return addr.getCanonicalHostName();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;

    }


    @Test
    public void runTests() throws Exception {
        initialize();
        runExportToJira();
        synchronizeChange();
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
        driver.findElement(By.id("admin_summary")).click();
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
        driver.get(baseUrl + "/browse/SL-1");
        Wait<WebDriver> wait = new WebDriverWait(driver, 30);
        WebElement element= wait.until(visibilityOfElementLocated(By.id("environment-val")));

        //TODO validate that the Browser value has been copied into Jira
    }

    public ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
        return new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver driver) {
                WebElement toReturn = driver.findElement(locator);
                if (toReturn.isDisplayed()) {
                    return toReturn;
                }
                return null;
            }
        };
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
//            sauceTunnelManager.closeTunnelsForPlan(DUMMY_KEY);
        }
    }

    /**
     * Returns an <code>InetAddress</code> object encapsulating what is most likely the machine's LAN IP address.
     * <p/>
     * This method is intended for use as a replacement of JDK method <code>InetAddress.getLocalHost</code>, because
     * that method is ambiguous on Linux systems. Linux systems enumerate the loopback network interface the same
     * way as regular LAN network interfaces, but the JDK <code>InetAddress.getLocalHost</code> method does not
     * specify the algorithm used to select the address returned under such circumstances, and will often return the
     * loopback address, which is not valid for network communication. Details
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
     * <p/>
     * This method will scan all IP addresses on all network interfaces on the host machine to determine the IP address
     * most likely to be the machine's LAN address. If the machine has multiple IP addresses, this method will prefer
     * a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the machine has one (and will return the
     * first site-local address if the machine has more than one), but if the machine does not hold a site-local
     * address, this method will return simply the first non-loopback address found (IPv4 or IPv6).
     * <p/>
     * If this method cannot find a non-loopback address using this selection algorithm, it will fall back to
     * calling and returning the result of JDK method <code>InetAddress.getLocalHost</code>.
     * <p/>
     *
     * @throws UnknownHostException If the LAN address of the machine cannot be found.
     */
    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        }
                        else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

}