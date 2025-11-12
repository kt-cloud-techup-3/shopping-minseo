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
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
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

	// 주문생성
	public static Order create(User user, String receiverName, String receiverAddress, String receiverMobile) {
		// Receiver 객체를 생성하여 주문 엔티티의 Private 생성자에 전달.
		Receiver receiver = new Receiver(receiverName, receiverAddress, receiverMobile);
		return new Order(receiver, user);
	}

	//하나의 오더는 여러개의 상품을 가질수있음
	// 1:N
	//하나의 상품은 여러개의 오더를 가질수있음
	// 1:N

	public void mapToOrderProduct(OrderProduct orderProduct) {
		this.orderProducts.add(orderProduct);
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
		// receiver 객체의 필드에 직접 접근하는 대신, Receiver 객체 내부의 update 메서드를 호출해야 합니다.
		// *Receiver 클래스에 update 메서드가 없으므로, Receiver 클래스도 수정해야 합니다.
		this.receiver.update(name, address, mobile);
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
