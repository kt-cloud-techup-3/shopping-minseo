package com.kt.domain.product;

import java.util.ArrayList;
import java.util.List;

import com.kt.common.BaseEntity;
import com.kt.domain.orderproduct.OrderProduct;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.Getter;

@Entity
@Getter
public class Product extends BaseEntity {
	private String name;
	private Long price;
	private Long stock;
	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	@OneToMany(mappedBy = "product")
	private List<OrderProduct> orderProducts = new ArrayList<>();

	//생성
	public static Product create(String name, Long price, Long stock, ProductStatus status) {
		Product product = new Product();
		product.name = name;
		product.price = price;
		product.stock = stock;
		product.status = status;
		return product;
	}

	//수정
	public void update(String name, Long price, Long stock, ProductStatus status) {
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.status = status;
	}

	//삭제
	public void delete() {
		this.status = ProductStatus.SOLD_OUT;
	}

	//조회(리스트, 단건)
	//상태변경
	public void changeStatus(ProductStatus status) {
		this.status = status;
	}

	//재고수량감소
	public void decreaseStock(Long quantity) {
		if (this.stock < quantity) {
			throw new IllegalArgumentException("재고가 부족합니다.");
		}
		this.stock -= quantity;
	}

	//재고수량증가
	public void increaseStock(Long quantity) {
		this.stock += quantity;
	}
}
