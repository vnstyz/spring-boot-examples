package org.example.taobao.product.init;

import org.example.taobao.product.entity.Product;
import org.example.taobao.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 初始化演示商品数据，方便本地体验商品详情和下单流程。
 */
@Component
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductDataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 启动时若商品表为空则插入演示数据。
     */
    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Product phone = new Product();
        phone.setTitle("iPhone 16 Pro");
        phone.setSubTitle("仿淘宝演示商品：高端手机");
        phone.setCoverUrl("https://img.example.com/iphone16pro.jpg");
        phone.setPrice(new BigDecimal("7999.00"));
        phone.setStock(200);
        phone.setEnabled(true);

        Product headset = new Product();
        headset.setTitle("AirPods Pro 3");
        headset.setSubTitle("仿淘宝演示商品：无线降噪耳机");
        headset.setCoverUrl("https://img.example.com/airpods3.jpg");
        headset.setPrice(new BigDecimal("1999.00"));
        headset.setStock(300);
        headset.setEnabled(true);

        Product keyboard = new Product();
        keyboard.setTitle("机械键盘 K99");
        keyboard.setSubTitle("仿淘宝演示商品：办公电竞两用");
        keyboard.setCoverUrl("https://img.example.com/keyboard-k99.jpg");
        keyboard.setPrice(new BigDecimal("499.00"));
        keyboard.setStock(500);
        keyboard.setEnabled(true);

        productRepository.saveAll(List.of(phone, headset, keyboard));
    }
}
