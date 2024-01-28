package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus status;

    // == 연관관계 편의 메서드
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * 생성 메서드
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        Arrays.stream(orderItems).forEachOrdered(order::addOrderItem);
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    /**
     * 비즈니스 메서드
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("배송 완료된 주문은 취소할 수 없습니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    /**
     * 주문 전체 가격 조회
     */
    public int getTotalPrice(){
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
