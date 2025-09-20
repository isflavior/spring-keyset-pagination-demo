package com.example.pagination;

import com.example.pagination.entity.Product;
import com.example.pagination.entity.ProductName;
import com.example.pagination.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.stream.IntStream;

@Configuration
public class DataLoader {

  @Bean
  CommandLineRunner seed(ProductRepository repo) {
    return args -> {
      if (repo.count() > 0) return;
      Random rnd = new Random(42);
      String[] bases = {"Mouse", "Keyboard", "Headset", "Monitor", "USB Cable", "Charger", "Webcam", "Lamp", "Stand", "Mat", "Speaker", "SSD", "HDD", "CPU", "GPU", "RAM", "Router", "Switch", "Microphone", "Tripod"};

      IntStream.range(0, 200).forEach(i -> {
        Product randomProduct = new Product();
        randomProduct.setPrice(BigDecimal.valueOf(5 + rnd.nextInt(400) + rnd.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP));
        randomProduct.setCreatedAt(Instant.now().minusSeconds(rnd.nextInt(60 * 60 * 24 * 90)));
        randomProduct.setBarcode(String.format("%013d", Math.abs(rnd.nextLong()) % 10000000000000L));

        ProductName mainName = new ProductName(randomProduct, bases[rnd.nextInt(bases.length)], 0);
        randomProduct.getNames().add(mainName);
        repo.save(randomProduct);
      });
    };
  }
}
