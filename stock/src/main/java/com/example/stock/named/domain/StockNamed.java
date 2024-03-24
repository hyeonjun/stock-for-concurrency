package com.example.stock.named.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class StockNamed {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "stock_uuid", unique = true, nullable = false)
  private String uuid;
  private Long productId;
  private Long quantity;

  public StockNamed(String uuid, Long productId, Long quantity) {
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
    final StringBuilder sb = new StringBuilder("StockNamed{");
    sb.append("id=").append(id);
    sb.append(", uuid=").append(uuid);
    sb.append(", productId=").append(productId);
    sb.append(", quantity=").append(quantity);
    sb.append('}');
    return sb.toString();
  }
}
