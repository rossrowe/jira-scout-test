package com.example.tests;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class SyncTest {
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
	public void testSync() throws Exception {
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
		driver.findElement(By.cssSelector("#browse_link_drop > span")).click();
		driver.findElement(By.id("admin_main_proj_link_lnk")).click();
		driver.findElement(By.linkText("SL-1")).click();
		driver.findElement(By.cssSelector("span.icon.drop-menu")).click();
		driver.findElement(By.id("connector-issue-links")).click();
		driver.findElement(By.id("connector-pull-changes-1-1")).click();
		driver.findElement(By.id("key-val")).click();
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
