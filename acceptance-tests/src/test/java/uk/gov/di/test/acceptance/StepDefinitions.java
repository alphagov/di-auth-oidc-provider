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

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StepDefinitions {

    private WebDriver driver;

    private String emailAddress;
    private String password;

    @Before
    public void setupWebdriver() {
        driver = new FirefoxDriver();
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
        driver.get("http://localhost:8081");
    }

    @And("the user clicks {string}")
    public void theUserClick(String buttonName) {
        WebElement button = driver.findElement(By.id(buttonName));
        button.click();
    }

    @Then("the user is taken to the Identity Provider Login Page")
    public void theUserIsTakenToTheIdentityProviderLoginPage() {
        assertEquals("/login", URI.create(driver.getCurrentUrl()).getPath());
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
}
