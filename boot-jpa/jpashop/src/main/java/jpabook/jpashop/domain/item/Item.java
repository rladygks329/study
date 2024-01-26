package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    @Column(name = "stock_quantity")
    private int stockQuantitiy;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // -- 비즈니스 로직 --
    /*
    * stock 증가
    * */
    public void addStockQuantity(int quantity){
        this.stockQuantitiy += quantity;
    }

    /*
    * stock 감소
    * */
    public void removeStockQantity(int quantity){
        int result = this.stockQuantitiy - quantity;
        if(result < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantitiy = result;
    }
}
