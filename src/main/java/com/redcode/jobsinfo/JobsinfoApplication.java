package com.redcode.jobsinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EntityScan(basePackages = {"com.redcode.jobsinfo.database.entity.model"})
@EnableJpaRepositories(basePackages = {"com.redcode.jobsinfo.repository"})
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware", dateTimeProviderRef = "dateTimeProvider")
@SpringBootApplication
public class JobsinfoApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(JobsinfoApplication.class, args);
	}

}
