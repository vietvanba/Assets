package com.nashtech.AssetManagement_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class CategoryEntity {
    @Id
    @Column(name = "prefix")
    private String prefix;

    @Column(name ="name", unique = true)
    private String name;

    @OneToMany(mappedBy = "categoryEntity")
    private List<AssetEntity> assetEntities = new ArrayList<>();

}
