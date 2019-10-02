package com.redcode.jobsinfo.database.entity.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "country")
@NoArgsConstructor
@Data
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_id_seq")
    @SequenceGenerator(name = "country_id_seq", sequenceName = "country_id_seq", allocationSize = 1)
    private Long id;

    @Size(max = 255)
    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "code")
    private String code;

    @Size(max = 20)
    @Column(name = "i_dialing")
    private String iDialing;

    @Column(name = "block")
    private boolean block;
}
