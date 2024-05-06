package com.loonycorn.learningselenium;

import com.loonycorn.learningselenium.pages.*;
import com.loonycorn.learningselenium.utils.DriverFactory;
import io.qameta.allure.*;
import io.qameta.allure.testng.Tag;
import io.qameta.allure.testng.Tags;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Checkout flow on Saucedemo")
public class PageObjectModelTest {

    private static final String SITE =
            "https://www.saucedemo.com/";

    private WebDriver driver;

    private LoginPage loginPage;
    private ProductsPage productsPage;
    private ProductPage productPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private FinalCheckoutPage finalCheckoutPage;
    private OrderCompletionPage orderCompletionPage;


    @BeforeClass
    public void setUp() {
        driver = DriverFactory.createDriver(DriverFactory.BrowserType.CHROME);

        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        productPage = new ProductPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
        finalCheckoutPage = new FinalCheckoutPage(driver);
        orderCompletionPage = new OrderCompletionPage(driver);

        driver.get(SITE);
    }

    private static void delay() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Feature("Login flow")
    @Story("Login")
    @Description("Test to verify login functionality")
    @Severity(SeverityLevel.BLOCKER)
    @Link("https://www.saucedemo.com/")
    @Tag("login")
    @Owner("Charles Darwin")
    @Step("Login and verify")
    @Test
    public void testLogin() {
        loginPage.login("standard_user", "secret_sauce");

        Assert.assertTrue(productsPage.isPageOpened(), "Login failed!");

        delay();
    }

    @Feature("Add products flow")
    @Story("Add products")
    @Description("Test to add a backpack to the cart")
    @Severity(SeverityLevel.NORMAL)
    @Link("https://www.saucedemo.com/inventory.html")
    @Tags({@Tag("products"), @Tag("add product")})
    @Owner("Jenny Li")
    @Step("Add product to cart")
    @Test(dependsOnMethods = "testLogin")
    public void testAddBackpackToCart() {
        productsPage.navigateToProductPage("Sauce Labs Backpack");

        productPage.addToCart();

        Allure.step("Add item to cart");

        Assert.assertEquals(productPage.getButtonText(), "Remove",
                "Button text did not change");

        delay();

        Allure.step("Navigate back to products page");

        driver.navigate().back();
    }

    @Feature("Add products flow")
    @Story("Add products")
    @Description("Test to add a fleece jacket to the cart")
    @Severity(SeverityLevel.NORMAL)
    @Link("https://www.saucedemo.com/inventory.html")
    @Tags({@Tag("products"), @Tag("add product")})
    @Owner("Jenny Li")
    @Step("Add product to cart")
    @Test(dependsOnMethods = "testAddBackpackToCart")
    public void testAddFleeceJacketToCart() {
        productsPage.navigateToProductPage("Sauce Labs Fleece Jacket");

        productPage.addToCart();

        Allure.step("Add item to cart");

        Assert.assertEquals(productPage.getButtonText(), "Remove",
                "Button text did not change");

        delay();

        Allure.step("Navigate back to products page");

        driver.navigate().back();
    }

    @Feature("View cart flow")
    @Story("View cart")
    @Description("Test to verify the cart contents")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://www.saucedemo.com/cart.html")
    @Tags({@Tag("cart"), @Tag("checkout")})
    @Owner("Charles Darwin")
    @Step("Validate items in cart")
    @Test(dependsOnMethods = {"testAddBackpackToCart", "testAddFleeceJacketToCart"})
    public void testCart() {
        Allure.step("Navigate to cart and verify state", step -> {
            productsPage.navigateToCart();

            Assert.assertTrue(cartPage.isPageOpened(), "Cart page not loaded");
            Assert.assertEquals(cartPage.getCartItemCount(), "2", "Incorrect number of items in the cart");
            Assert.assertEquals(cartPage.getContinueButtonText(), "Checkout",
                    "Incorrect button text on the cart page");

            Allure.addAttachment("Cart item count", cartPage.getCartItemCount());
        });

        Allure.step("Verify products in cart", step -> {
            Assert.assertTrue(cartPage.productInCart("Sauce Labs Backpack"));
            Assert.assertTrue(cartPage.productInCart("Sauce Labs Fleece Jacket"));
        });

        delay();
    }

    @Feature("Checkout flow")
    @Story("Checkout")
    @Description("Test to verify the checkout functionality")
    @Severity(SeverityLevel.MINOR)
    @Link("https://www.saucedemo.com/checkout-step-one.html")
    @Tag("checkout")
    @Owner("Charles Darwin")
    @Flaky
    @Step("Verify checkout page")
    @Test(dependsOnMethods = "testCart")
    public void testCheckout() {
        Allure.step("Navigate to checkout and enter details", step -> {
            cartPage.continueCheckout();

            Assert.assertTrue(checkoutPage.isPageOpened(), "Checkout page not loaded");
            checkoutPage.enterDetails("Nora", "Jones", "12345");
        });

        Allure.step("Verify entered details", step -> {
            Assert.assertEquals(checkoutPage.getFirstNameFieldValue(), "Nora",
                    "First name field value is incorrect");
            Assert.assertEquals(checkoutPage.getLastNameFieldValue(), "Jones",
                    "Last name field value is incorrect");
            Assert.assertEquals(checkoutPage.getZipCodeFieldValue(), "12345",
                    "Zip code field value is incorrect");
        });

        delay();
    }


    @Feature("Checkout flow")
    @Story("Checkout")
    @Description("Test to verify the final checkout functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://www.saucedemo.com/checkout-step-two.html")
    @Tag("checkout")
    @Owner("Charles Darwin")
    @Step("Verify final checkout page")
    @Test(dependsOnMethods = "testCheckout")
    public void testFinalCheckout() {
        checkoutPage.continueCheckout();

        Assert.assertTrue(finalCheckoutPage.isPageOpened(),
                "Checkout page not loaded");
        Assert.assertEquals(finalCheckoutPage.getPaymentInfoValue(),
                "SauceCard #31337");
        Assert.assertEquals(finalCheckoutPage.getShippingInfoValue(), 
                "Free Pony Express Delivery!");
        Assert.assertEquals(finalCheckoutPage.getTotalLabel(),
                "Total: $86.38");

        delay();
    }


    @Feature("Checkout flow")
    @Story("Order completion")
    @Description("Test to verify the order completion functionality")
    @Severity(SeverityLevel.TRIVIAL)
    @Link("https://www.saucedemo.com/checkout-complete.html")
    @Tags({@Tag("order completion"), @Tag("checkout")})
    @Owner("Jackson Smith")
    @Flaky
    @Step("Verify order completion page")
    @Test(dependsOnMethods = "testFinalCheckout")
    public void testOrderCompletion() {
        finalCheckoutPage.finishCheckout();

        Assert.assertEquals(orderCompletionPage.getHeaderText(), "Thank you for your order!");
        Assert.assertEquals(orderCompletionPage.getBodyText(),
                "Your order has been dispatched, and will arrive just as fast as the pony can get there!");

        delay();
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
