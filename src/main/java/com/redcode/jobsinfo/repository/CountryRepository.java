package com.redcode.jobsinfo.repository;

import com.redcode.jobsinfo.database.entity.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country>  findByBlock(boolean block);
}
