package com.example.stock.redis.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class StockRedis {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "stock_uuid", unique = true, nullable = false)
  private String uuid;
  private Long productId;
  private Long quantity;

  public StockRedis(String uuid, Long productId, Long quantity) {
    this.uuid = uuid;
    this.productId = productId;
    this.quantity = quantity;
  }

  public void decrease(Long quantity) {
    if (this.quantity - quantity < 0) {
      throw new RuntimeException("재고는 0개 미만이 될 수 없습니다.");
    }

    this.quantity -= quantity;
  }

  @Override
  public String toString() {
    return "StockRedis{" +
      "id=" + id +
      ", uuid='" + uuid + '\'' +
      ", productId=" + productId +
      ", quantity=" + quantity +
      '}';
  }
}
