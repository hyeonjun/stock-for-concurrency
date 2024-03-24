package com.example.stock.pessimistic.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class StockPessimistic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;
  private Long quantity;

  public StockPessimistic(Long productId, Long quantity) {
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
    final StringBuilder sb = new StringBuilder("StockPessimistic{");
    sb.append("id=").append(id);
    sb.append(", productId=").append(productId);
    sb.append(", quantity=").append(quantity);
    sb.append('}');
    return sb.toString();
  }
}
