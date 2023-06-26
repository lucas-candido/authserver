package br.pucpr.authserver.services

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServicesRepository : JpaRepository<Service, Long>
