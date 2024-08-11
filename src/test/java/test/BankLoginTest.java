package test;

import org.junit.jupiter.api.*;
import data.DataHelper;
import data.SQLHelper;
import page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.cleanAuthCodes;
import static data.SQLHelper.cleanDatabase;

public class BankLoginTest {
    LoginPage loginPage;
    @AfterEach
    void tearDown(){
        cleanAuthCodes();
    }
    @AfterAll
    static void tearDownAll(){
        cleanDatabase();
    }
    @BeforeEach
    void setUp(){
        loginPage = open("http://localhost:9999", LoginPage.class);
    }
    @Test
    void shouldSuccessfulLogin(){
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode);
    }
    @Test
    void shouldGetErrorNotificationIfLoginWithRandomUserWithoutAddingToBase(){
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
    }
    @Test
    void shouldGetErrorNotificationIfLoginWithExistUserAndRandomVerificationCode(){
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
    }
}
