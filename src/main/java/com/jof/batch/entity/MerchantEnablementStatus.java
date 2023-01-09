package com.jof.batch.entity;


import lombok.Data;
import lombok.Generated;

import javax.persistence.*;

@Entity
@Table
@Data
public class MerchantEnablementStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="mid")
    private String mid;

    @Column(name="isCustomer")
    private String customer;

}
