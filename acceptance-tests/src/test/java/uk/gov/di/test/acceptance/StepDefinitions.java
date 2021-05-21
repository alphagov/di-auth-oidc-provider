package uk.gov.di.test.acceptance;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class StepDefinitions {

    private static final String SELENIUM_URL = System.getenv().get("SELENIUM_URL");
    private static final URI IDP_URL = URI.create(
            System.getenv().getOrDefault("IDP_URL", "http://localhost:8080/")
    );
    private static final URI RP_URL = URI.create(
            System.getenv().getOrDefault("RP_URL","http://localhost:8081/")
    );

    private WebDriver driver;

    private String emailAddress;
    private String password;

    @Before
    public void setupWebdriver() throws MalformedURLException {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(true);
        if (SELENIUM_URL == null) {
            driver = new FirefoxDriver(firefoxOptions);
        } else {
            driver = new RemoteWebDriver(new URL(SELENIUM_URL), firefoxOptions);
        }
    }

    @After
    public void closeWebdriver() {
        driver.quit();
    }

    @Given("the services are running")
    public void theServicesAreRunning() {
    }

    @And("the user has valid credentials")
    public void theUserHasValidCredentials() {
        emailAddress = "joe.bloggs@digital.cabinet-office.gov.uk";
        password = "password";
    }

    @When("the user visit the stub relying party")
    public void theUserVisitTheStubRelyingParty() {
        driver.get(RP_URL.toString());
    }

    @And("the user clicks {string}")
    public void theUserClick(String buttonName) {
        WebElement button = driver.findElement(By.id(buttonName));
        button.click();
    }

    @Then("the user is taken to the Identity Provider Login Page")
    public void theUserIsTakenToTheIdentityProviderLoginPage() {
        assertEquals("/login", URI.create(driver.getCurrentUrl()).getPath());
        assertEquals(IDP_URL.getHost(), URI.create(driver.getCurrentUrl()).getHost());
        assertEquals("Sign-in to GOV.UK - Email Address", driver.getTitle());
    }

    @When("the user enters their email address")
    public void theUserEntersEmailAddress() {
        WebElement emailAddressField = driver.findElement(By.id("email"));
        emailAddressField.sendKeys(emailAddress);
        WebElement continueButton = driver.findElement(By.xpath("//button[text()='Continue']"));
        continueButton.click();
    }

    @Then("the user is prompted for password")
    public void theUserIsPromptedForPassword() {
        assertEquals("/login", URI.create(driver.getCurrentUrl()).getPath());
        assertEquals("Sign-in to GOV.UK - Password", driver.getTitle());
    }

    @When("the user enters their password")
    public void theUserEntersTheirPassword() {
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(password);
        WebElement continueButton = driver.findElement(By.xpath("//button[text()='Continue']"));
        continueButton.click();
    }

    @Then("the user is taken to the Success page")
    public void theUserIsTakenToTheSuccessPage() {
        assertEquals("/login/validate", URI.create(driver.getCurrentUrl()).getPath());
        assertEquals("Sign-in to GOV.UK - Success", driver.getTitle());
    }

    @When("the user clicks the {string} button")
    public void theUserClicksTheSpecifiedButton(String buttonText) {
        WebElement button = driver.findElement(By.xpath(String.format("//button[text()='%s']", buttonText)));
        button.click();
    }

    @Then("the user is taken to the Service User Info page")
    public void theUserIsTakenToTheServiceUserInfoPage() {
        assertEquals("/oidc/callback", URI.create(driver.getCurrentUrl()).getPath());
        assertEquals(RP_URL.getHost(), URI.create(driver.getCurrentUrl()).getHost());
        assertEquals(RP_URL.getPort(), URI.create(driver.getCurrentUrl()).getPort());
        assertEquals("Example - GOV.UK - User Info", driver.getTitle());
        WebElement emailDescriptionDetails = driver.findElement(By.id("user-info-email"));
        assertEquals(emailAddress, emailDescriptionDetails.getText().trim());
    }
}
