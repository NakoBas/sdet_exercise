import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.regex.Pattern;

public class exercise_01 {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://sdetchallenge.fetch.com/");
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void findFakeGoldBar() {
        int[] firstGroup = {0, 1, 2};
        int[] secondGroup = {3, 4, 5};
        int[] thirdGroup = {6, 7, 8};

        // Weigh first two groups
        String result = weighGroups(firstGroup, secondGroup);

        if (result.equals("=")) {
            // If equal, weigh any two bars from the third group
            result = weighGroups(new int[]{thirdGroup[0]}, new int[]{thirdGroup[1]});

            int fakeBarIndex = result.equals("=") ? thirdGroup[2] : thirdGroup[result.equals("<") ? 0 : 1];
            clickOnGoldBar(fakeBarIndex);

        } else {
            // Determine which group has the fake bar and weigh two bars within that group
            int[] suspectGroup = result.equals("<") ? firstGroup : secondGroup;
            result = weighGroups(new int[]{suspectGroup[0]}, new int[]{suspectGroup[1]});
            int fakeBarIndex = result.equals("=") ? suspectGroup[2] : suspectGroup[result.equals("<") ? 0 : 1];
            clickOnGoldBar(fakeBarIndex);
        }


        Alert alert = driver.switchTo().alert();
        Assert.assertEquals(alert.getText(), "Yay! You find it!");
        System.out.println(alert.getText());
        alert.accept();
    }

    private String weighGroups(int[] leftGroup, int[] rightGroup) {
        for (int i = 0; i < leftGroup.length; i++) {
            enterNumber(leftGroup[i], "left", i);
        }
        for (int i = 0; i < rightGroup.length; i++) {
            enterNumber(rightGroup[i], "right", i);
        }
        driver.findElement(By.id("weigh")).click();
        WebElement resultElement = driver.findElement(By.cssSelector(".result #reset"));
        wait.until(ExpectedConditions.textMatches(By.cssSelector(".result #reset"), Pattern.compile("[<>=]")));
        String result = resultElement.getText();
        resetScale();
        return result;
    }

    private void enterNumber(int number, String side, int position) {
        WebElement input = driver.findElement(By.id(side + "_" + position));
        input.clear();
        input.sendKeys(String.valueOf(number));
    }

    private void resetScale() {
        WebElement resetButton = driver.findElement(By.cssSelector("#reset:first-child"));
        resetButton.click();
    }

    private void clickOnGoldBar(int barIndex) {
        WebElement barButton = driver.findElement(By.id("coin_" + barIndex));
        barButton.click();
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {

        Thread.sleep(10000);
        driver.quit();
    }
}