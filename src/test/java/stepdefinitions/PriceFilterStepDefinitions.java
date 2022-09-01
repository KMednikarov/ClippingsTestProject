package stepdefinitions;

import io.cucumber.java.*;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.PropertyFileReader;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceFilterStepDefinitions {
    private static final String propertiesPath = "src/test/resources/config/PriceFilter.properties";
    private static Map<String, String> properties;
    private static WebDriver driver;
    private Scenario scenario;

    @Before
    public void beforeScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @BeforeAll
    public static void setup() {
        try {
            properties = PropertyFileReader.readPropFileToHashMap(propertiesPath);
        } catch (IOException e) {
            //TODO handle exception
            throw new RuntimeException(e);
        }
        driver = new ChromeDriver();
    }

    @Given("the user opens the search page$")
    public void openSearchPage() {
        String url = properties.get("searchUrl");
        Assert.assertNotNull(url);
        driver.get(url);
        scenario.log("Search page opened.");
    }

    @When("the user enters positive numbers in {double} and {double} fields")
    public void userFillasMinAndMax(double minPrice, double maxPrice) {
        String minBtnId = properties.get("minBtnId");
        String maxBtnId = properties.get("maxBtnId");

        WebElement minButton = driver.findElement(By.id(minBtnId));
        WebElement maxButton = driver.findElement(By.id(maxBtnId));

        minButton.sendKeys(String.valueOf(minPrice));
        maxButton.sendKeys(String.valueOf(maxPrice));
        maxButton.submit();
        scenario.log("Min and max filters applied: min=" + minPrice + ", max=" + maxPrice);
    }

    @And("the search results are received$")
    public void receiveSearchResults() throws InterruptedException {

        String maxBtnId = properties.get("maxBtnId");

        WebElement maxButton = driver.findElement(By.id(maxBtnId));
        new WebDriverWait(driver, Duration.of(2, ChronoUnit.SECONDS)).until(ExpectedConditions.visibilityOf(maxButton));

//        wait.until(ExpectedConditions.visibilityOfAllElements());
        scenario.log("Search results received");
    }

    @Then("the price of the products should be between {double} and {double}")
    public void validatePricesBetweenMinAndMax(double minPrice, double maxPrice) {
        String searchResultsId = properties.get("searchResultsId");
        String priceTagSelector = properties.get("priceTagSelector");

        List<WebElement> searchResults = driver.findElement(By.id(searchResultsId)).findElements(By.cssSelector(priceTagSelector));
        for (WebElement result : searchResults) {
            double price = parsePriceTag(result.getText());
            Assert.assertTrue("[Fail] There are product prices that are not in the min-max range"
                    , price >= minPrice && price <= maxPrice);
        }
        scenario.log("All product prices are between min and max: min=" + minPrice + ", max=" + maxPrice);
    }

    private double parsePriceTag(String priceTag) {
        String pricePattern = "([0-9]+[.,][0-9]+)";
        String price = "";
        Pattern r = Pattern.compile(pricePattern);
        Matcher matcher = r.matcher(priceTag);
        if (matcher.find()) {
            price = matcher.group(1);
        }

        return Double.parseDouble(price);
    }

    @AfterAll
    private static void closeWebDriver() {
        driver.close();
    }

}
