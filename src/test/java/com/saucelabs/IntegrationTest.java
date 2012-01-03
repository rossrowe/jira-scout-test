package com.example.tests;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class IntegrationTest {
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		baseUrl = "http://imac.local:2990/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testIntegration() throws Exception {
		driver.get(baseUrl + "/jira/secure/AdminSummary.jspa");
		driver.findElement(By.cssSelector("#admin_plugins_menu_drop > span")).click();
		driver.findElement(By.xpath("//a[@id='upm_section']")).click();
		driver.findElement(By.cssSelector("#admin_plugins_menu_drop > span")).click();
		driver.findElement(By.id("customwareconnectorconnectionwebitem")).click();
		driver.findElement(By.name("submit")).click();
		driver.findElement(By.id("connectorId")).clear();
		driver.findElement(By.id("connectorId")).sendKeys("Sauce Labs");
		driver.findElement(By.id("add_submit")).click();
		driver.findElement(By.linkText("Manage Application Links")).click();
		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys("Sauce Labs");
		driver.findElement(By.name("Add")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
		driver.findElement(By.name("rpcurl")).clear();
		driver.findElement(By.name("rpcurl")).sendKeys("https://saucelabs.com");
		driver.findElement(By.name("displayurl")).clear();
		driver.findElement(By.name("displayurl")).sendKeys("https://saucelabs.com");
		driver.findElement(By.name("rpcurl")).clear();
		driver.findElement(By.name("rpcurl")).sendKeys("https://saucelabs.com");
		driver.findElement(By.name("Add")).click();
		driver.findElement(By.id("configauth-751dd8c8-be04-3477-b92a-5130cd39c6dd")).click();
		driver.findElement(By.id("saucelabs.username")).clear();
		driver.findElement(By.id("saucelabs.username")).sendKeys("rossco_9_9");
		driver.findElement(By.id("saucelabs.token")).clear();
		driver.findElement(By.id("saucelabs.token")).sendKeys("44f0744c-1689-4418-af63-560303cbb37b");
		driver.findElement(By.cssSelector("input.button")).click();
		driver.findElement(By.cssSelector("#customwareconnectormappingwebitem_tab > strong")).click();
		driver.findElement(By.cssSelector("strong")).click();
		driver.findElement(By.id("connectorId")).clear();
		driver.findElement(By.id("connectorId")).sendKeys("Sauce Labs");
		driver.findElement(By.id("add_submit")).click();
		new Select(driver.findElement(By.id("applink"))).selectByVisibleText("Sauce Labs");
		driver.findElement(By.id("add_submit")).click();
		driver.findElement(By.id("launch_wizard")).click();
		driver.findElement(By.cssSelector("#admin_project_menu_drop > span")).click();
		driver.findElement(By.id("admin_project_menu")).click();
		driver.findElement(By.id("admin_project_menu")).click();
		driver.findElement(By.id("view_projects")).click();
		driver.findElement(By.id("admin_summary")).click();
		driver.findElement(By.id("add_first_project")).click();
		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys("Sauce Labs");
		driver.findElement(By.name("key")).clear();
		driver.findElement(By.name("key")).sendKeys("SL");
		driver.findElement(By.id("add-project-submit")).click();
		driver.findElement(By.cssSelector("#admin_plugins_menu_drop > span")).click();
		driver.findElement(By.id("customwareconnectorconnectionwebitem")).click();
		driver.findElement(By.linkText("Wizard")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [addSelection]]
		driver.findElement(By.name("setupScreens")).click();
		driver.findElement(By.id("create_config")).click();
		driver.findElement(By.linkText("click here")).click();
		driver.findElement(By.xpath("//div[@id='mainNav']/ul/li[3]/table/tbody/tr/td[2]")).click();
		driver.findElement(By.xpath("//div[@id='main']/div[2]/center/div[3]/table/tbody/tr/td/table/tbody/tr/td[2]/ul/li[2]/a")).click();
		driver.findElement(By.name("base_url")).click();
		driver.findElement(By.name("base_url")).clear();
		driver.findElement(By.name("base_url")).sendKeys("http://imac.local:2990/jira");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		driver.findElement(By.linkText("Export to JIRA")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [selectWindow]]
		new Select(driver.findElement(By.id("projectField"))).selectByVisibleText("Sauce Labs");
		driver.findElement(By.name("create")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
		driver.findElement(By.id("mast_login_username")).clear();
		driver.findElement(By.id("mast_login_username")).sendKeys("rossco_9_9");
		driver.findElement(By.id("mast_login_password")).clear();
		driver.findElement(By.id("mast_login_password")).sendKeys("piasal");
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
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
