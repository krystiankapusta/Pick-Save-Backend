package com.picksave.product_service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fetch_job")
public class FetchJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "shop_name", nullable = false)
    private String shopName;
    @Column(name = "last_page", nullable = false)
    private int lastPage = 0;
    @Enumerated(EnumType.STRING)
    @Column(name = "fetch_job_status", nullable = false)
    private FetchJobStatus fetchJobStatus = FetchJobStatus.RUNNING;

    public FetchJob(String shopName, int lastPage, FetchJobStatus fetchJobStatus) {
        this.shopName = shopName;
        this.lastPage = lastPage;
        this.fetchJobStatus = fetchJobStatus;
    }
}
