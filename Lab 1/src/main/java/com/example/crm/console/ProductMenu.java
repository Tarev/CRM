package com.example.crm.console;

import com.example.crm.model.Product;
import com.example.crm.service.ProductService;

import java.math.BigDecimal;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class ProductMenu implements MenuSection {

    private final ProductService productService;
    private final ConsoleInput input;

    public ProductMenu(ProductService productService, ConsoleInput input) {
        this.productService = productService;
        this.input = input;
    }

    @Override
    public String getTitle() {
        return "Продукты";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Продукты ---");
            System.out.println("1. Показать все");
            System.out.println("2. Добавить");
            System.out.println("3. Найти по названию");
            System.out.println("4. Показать дешевле указанной цены");
            System.out.println("5. Удалить по id");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ");
            switch (choice) {
                case 1:
                    System.out.println(productService.describeAll());
                    break;
                case 2:
                    String name = input.readString("Название: ");
                    BigDecimal price = input.readMoney("Цена: ");
                    try {
                        System.out.println("Добавлено: " + productService.add(new Product(name, price)));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println(productService.describe(productService.findByName(input.readString("Часть названия: "))));
                    break;
                case 4:
                    System.out.println(productService.describe(productService.findWithPriceUpTo(input.readMoney("Максимальная цена: "))));
                    break;
                case 5:
                    if (productService.deleteById(input.readLong("id для удаления: "))) {
                        System.out.println("Удалено");
                    } else {
                        System.out.println("Объект с таким id не найден");
                    }
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Нет такого пункта");
            }
        }
    }
}
