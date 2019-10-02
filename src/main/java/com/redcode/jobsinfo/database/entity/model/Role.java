package com.redcode.jobsinfo.database.entity.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "user_roles")
@NoArgsConstructor
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_roles_id_seq")
    @SequenceGenerator(name = "user_roles_id_seq", sequenceName = "user_roles_id_seq", allocationSize = 1)
    private Long id;

    @Size(max = 50)
    @Column(name = "role", nullable = false, length = 50)
    private String role;

}
