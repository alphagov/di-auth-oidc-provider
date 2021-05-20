package uk.gov.di.test.acceptance;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class StepDefinitions {

    private WebDriver driver;

    @Before
    public void setupWebdriver() {
        driver = new FirefoxDriver();
    }

    @After
    public void closeWebdriver() {
        driver.quit();
    }

    @Given("The services are running")
    public void theServicesAreRunning() {
    }

    @And("has not signed into the IDP")
    public void hasNotSignedIntoTheIDP() {
    }

    @When("the user visit the stub relying party")
    public void theUserVisitTheStubRelyingParty() {
        driver.get("http://localhost:8081");
        new WebDriverWait(driver,30L).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("govuk-signin-button")) != null;
            }
        });
    }

    @And("the user clicks {string}")
    public void theUserClick(String buttonName) {
        WebElement button = driver.findElement(By.id(buttonName));
        button.click();
    }

    @Then("The user is taken to the Identity Provider Login Page")
    public void theUserIsTakenToTheIdentityProviderLoginPage() {
        new WebDriverWait(driver,30L).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return URI.create(driver.getCurrentUrl()).getPath().equals("/login");
            }
        });
    }
}
