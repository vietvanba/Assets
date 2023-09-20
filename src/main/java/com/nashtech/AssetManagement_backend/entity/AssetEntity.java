package com.nashtech.AssetManagement_backend.entity;

import com.nashtech.AssetManagement_backend.generators.AssetCodeGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "asset")
public class AssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asset_seq")
    @GenericGenerator(
            name = "asset_seq",
            strategy = "com.nashtech.AssetManagement_backend.generators.AssetCodeGenerator",
            parameters = {
                    @Parameter(name = AssetCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%06d")})
    @Column(name = "asset_code")
    private String assetCode;

    @Column(name = "name")
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(length = 60,name = "state")
    private AssetState state;

    @Column(name = "specification")
    private String specification;

    @Column(name = "installed_date")
    private Date installedDate;

    @ManyToOne
    @JoinColumn(name="category_id")
    private CategoryEntity categoryEntity;

    @OneToMany(mappedBy = "assetEntity")
    private List<AssignmentEntity> assignmentEntities = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="location_id")
    private LocationEntity location;
}

