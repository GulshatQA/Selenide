package ru.netology.selenide;


import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardDeliveryTest {

    public static String date(long addToDays) {
        return LocalDate.now().plusDays(addToDays).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
    }

    // Тест, когда все поля заполнены согласно требованиям. Заявка успешно отправлена
    @Test
    void testWhenSuccessfullyCompleted()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='notification'] .notification__title").shouldHave(text("Успешно!"), Duration.ofSeconds(15)).shouldBe(visible);
        $("[data-test-id='notification'] .notification__content").shouldHave(text("Встреча успешно забронирована на " + date), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда город доставки не является административным центром cубъекта РФ.
    @Test
    void testWhenCityIsNotOnTheList()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Чайковский");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='city'] .input__sub").shouldHave(text("Доставка в выбранный город недоступна"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда поле "Город доставки" заполнено на латинице.
    @Test
    void testWhenCityIsLatin()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Moscow");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='city'] .input__sub").shouldHave(text("Доставка в выбранный город недоступна"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда в поле "Город" использован спец. символ, кроме разрешённого дефиса
    @Test
    void testWhenCityIsSymbol()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт_Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='city'] .input__sub").shouldHave(text("Доставка в выбранный город недоступна"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда поле "Город" незаполнено.
    @Test
    void testWhenCityIsZero()  {
        String date = date(3);
        //$("[data-test-id='city'] .input__control").setValue("Чайковский");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='city'] .input__sub").shouldHave(text("Поле обязательно для заполнения"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда поле "Дата встречи" незаполнено.
    @Test
    void testWhenDateIsZero()  {
        //String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='date'] .input__sub").shouldHave(text("Неверно введена дата"));
    }

    // Тест, когда менее трёх дней до "Даты встречи" от текущей даты.
    @Test
    void testWhenDateIsLessThanThreeDays()  {
        String date = date(2);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='date'] .input__sub").shouldHave(text("Заказ на выбранную дату невозможен"));
    }

    // Тест, когда "Дата встречи" назначена через месяц от текущей даты.
    @Test
    void testWhenDateIsLastMonth()  {
        String date = date(30);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='notification'] .notification__title").shouldHave(text("Успешно!"), Duration.ofSeconds(15)).shouldBe(visible);
        $("[data-test-id='notification'] .notification__content").shouldHave(text("Встреча успешно забронирована на " + date), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда поле "Фамилия и имя" незаполнено.
    @Test
    void testWhenNameIsZero()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        //$("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'] .input__sub").shouldHave(text("Поле обязательно для заполнения"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда поле "Фамилия и имя" заполнено на латинице.
    @Test
    void testWhenNameIsLatin()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Petrov Ivan");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'] .input__sub").shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда в написании имени и фамилии встречается буква "Ё".
    @Test
    void testWhenNameWithYO()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Соловьёв Семён");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'] .input__sub").shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда в поле "Фамилия и имя" использованы символы, кроме разрешённого дефиса.
    @Test
    void testWhenNameIsSymbol()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван!");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'] .input__sub").shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда в поле "Фамилия и имя" использованы цифры.
    @Test
    void testWhenNameIsNumbers()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров1 Иван");
        $("[data-test-id='phone'] .input__control").setValue("+79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'] .input__sub").shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда в поле "Телефон" указано 12 цифр.
    @Test
    void testWhenPhoneIs12Numbers()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+798780080800");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'] .input__sub").shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда в поле "Телефон" указано 10 цифр.
    @Test
    void testWhenPhoneIs10Numbers()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+7987800808");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'] .input__sub").shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда в поле "Телефон" не указан символ "+".
    @Test
    void testWhenPhoneIsNotSymbolPlus()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("79878008080");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'] .input__sub").shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда в поле "Телефон" указаны дополнительные символы.
    @Test
    void testWhenPhoneIsSymbolMinus()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("7-987-800-80-80");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'] .input__sub").shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда поле "Телефон" пустое.
    @Test
    void testWhenPhoneIsZero()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        //$("[data-test-id='phone'] .input__control").setValue("+798780080800");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'] .input__sub").shouldHave(text("Поле обязательно для заполнения"), Duration.ofSeconds(15)).shouldBe(visible);
    }

    // Тест, когда чек-бокс не помечен галочкой.
    @Test
    void testWhenCheckboxIsNotClick()  {
        String date = date(3);
        $("[data-test-id='city'] .input__control").setValue("Санкт-Петербург");
        $("[data-test-id='date'] .input__control").doubleClick().sendKeys(date);
        $("[data-test-id='name'] .input__control").setValue("Петров Иван");
        $("[data-test-id='phone'] .input__control").setValue("+798780080800");
        //$("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        boolean agreement = $("[data-test-id='agreement']").isDisplayed();
        assertEquals (true, agreement);
    }
}
