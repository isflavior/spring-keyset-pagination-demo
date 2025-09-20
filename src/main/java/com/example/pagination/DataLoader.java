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
      String[] bases = {"Mouse","Keyboard","Headset","Monitor","USB Cable","Charger","Webcam","Lamp","Stand","Mat","Speaker","SSD","HDD","CPU","GPU","RAM","Router","Switch","Microphone","Tripod"};
      IntStream.range(0, 11).forEach(i -> {
        Product p = new Product(
            BigDecimal.valueOf(5 + rnd.nextInt(400) + rnd.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP),
            Instant.now().minusSeconds((long) rnd.nextInt(60*60*24*90))
        );
        // Main and alternate names
        String main = bases[rnd.nextInt(bases.length)] + " " + (100 + rnd.nextInt(900));
        ProductName mainName = new ProductName(p, main, 0);
        p.getNames().add(mainName);
        if (rnd.nextBoolean()) {
          p.getNames().add(new ProductName(p, main + " Pro", 1));
        }
        repo.save(p);
      });
    };
  }
}
