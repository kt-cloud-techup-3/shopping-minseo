package com.kt.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kt.common.BaseEntity;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.user.User;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
	@Embedded
	private Receiver receiver;
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	private LocalDateTime deliveredAt;

	// 연관관계
	// 주문 <-> 회원
	// N : 1 => 다대일
	// ManyToOne
	// FK => 많은 쪽에 생김
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "order")
	private List<OrderProduct> orderProducts = new ArrayList<>();

	private Order(Receiver receiver, User user) {
		this.receiver = receiver;
		this.user = user;
		this.deliveredAt = LocalDateTime.now().plusDays(3);
		this.status = OrderStatus.PENDING;
	}

	public static Order create(Receiver receiver, User user) {
		return new Order(
			receiver,
			user
		);
	}

	public void mapToOrderProduct(OrderProduct orderProduct) {
		this.orderProducts.add(orderProduct);
	}

	//하나의 오더는 여러개의 상품을 가질수있음
	// 1:N
	//하나의 상품은 여러개의 오더를 가질수있음
	// 1:N

	// 주문생성
	public static Order create(User user, String receiverName, String receiverAddress, String receiverMobile) {
		Order order = new Order();
		order.user = user;
		order.receiverName = receiverName;
		order.receiverAddress = receiverAddress;
		order.receiverMobile = receiverMobile;
		order.status = OrderStatus.PENDING;
		return order;
	}

	// public void addOrderProduct(Product product, Long quantity) {
	// 	OrderProduct orderProduct = OrderProduct.create(this, product, quantity);
	// 	this.orderProducts.add(orderProduct);
	// 	product.decreaseStock(quantity); // 재고 차감
	// }

	// 주문상태변경
	public void changeStatus(OrderStatus status) {
		this.status = status;
	}

	// 주문생성완료재고차감
	public void completeOrder() {
		this.status = OrderStatus.COMPLETED;
		this.deliveredAt = LocalDateTime.now();
	}

	// 배송받는사람정보변경
	public void updateReceiverInfo(String name, String address, String mobile) {
		this.receiverName = name;
		this.receiverAddress = address;
		this.receiverMobile = mobile;
	}

	// 주문취소
	public void cancel() {
		if (this.status == OrderStatus.CANCELLED) {
			throw new IllegalStateException("이미 취소된 주문입니다.");
		}
		this.status = OrderStatus.CANCELLED;
		// 취소 시 재고 복원
		for (OrderProduct op : orderProducts) {
			op.getProduct().increaseStock(op.getQuantity());
		}
	}
}
