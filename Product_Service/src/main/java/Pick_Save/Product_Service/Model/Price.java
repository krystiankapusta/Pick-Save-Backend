package Pick_Save.Product_Service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "price")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    private double amount;
    @Column(nullable = false, length = 3)
    private String currency;
    private String shop;
    private String source;
    private boolean approved;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public Price(double amount, String currency, String shop) {
        this.amount = amount;
        this.currency = currency;
        this.shop = shop;
    }
}
