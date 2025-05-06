package com.assignment.ecommerce_rookie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 5, message = "Street name must contain at least 5 characters")
    @Column(name = "street")
    private String street;

    @NotBlank
    @Size(min = 5, message = "Ward must contain at least 5 characters")
    @Column(name = "ward")
    private String ward;

    @NotBlank
    @Size(min = 5, message = "District must be at least 5 characters")
    @Column(name = "district")
    private String district;

    @NotBlank
    @Size(min = 5, message = "Province/City must be at least 5 characters")
    @Column(name = "province")
    private String province;

    @NotBlank
    @Size(min = 5, message = "Country must be at least 5 characters")
    @Column(name = "country")
    private String country;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Address(String street, String ward, String district, String province, String country) {
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.country = country;
    }
}
