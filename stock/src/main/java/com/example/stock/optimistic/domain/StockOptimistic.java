package com.example.stock.optimistic.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class StockOptimistic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;
  private Long quantity;

  @Version
  private Long version;

  public StockOptimistic(Long productId, Long quantity) {
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
    final StringBuilder sb = new StringBuilder("StockOptimistic{");
    sb.append("id=").append(id);
    sb.append(", productId=").append(productId);
    sb.append(", quantity=").append(quantity);
    sb.append(", version=").append(version);
    sb.append('}');
    return sb.toString();
  }
}
