package br.pucpr.authserver.services

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/services")
@Tag(name = "APIs de Serviços")
class ServicesController(private val servicesRepository: ServicesRepository) {

    @GetMapping("/")
    @Operation(summary = "Lista todos os serviços", description = "Retorna uma lista com todos os serviços disponíveis para contratação")
    fun getAllServices(): List<Service> {
        return servicesRepository.findAll()
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lista um serviço específico", description = "Retorna uma serviço baseado no ID informado na requisição")
    fun getService(@PathVariable id: Long): ResponseEntity<Service> {
        val service = servicesRepository.findById(id)
        return if (service.isPresent) {
            ResponseEntity.ok(service.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Cria um serviço", description = "Retorna o serviço criado")
    fun createService(@Valid @RequestBody service: Service): ResponseEntity<Service> {
        val createdService = servicesRepository.save(service)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService)
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Atualiza um serviço", description = "Retorna o serviço atualizado")
    fun updateService(
        @PathVariable id: Long,
        @Valid @RequestBody updatedService: Service
    ): ResponseEntity<Service> {
        val service = servicesRepository.findById(id)
        return if (service.isPresent) {
            val updated = servicesRepository.save(updatedService.copy(id = id))
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Deleta um serviço", description = "Retorna código 200 em caso de sucesso")
    fun deleteService(@PathVariable id: Long): ResponseEntity<Void> {
        return if (servicesRepository.existsById(id)) {
            servicesRepository.deleteById(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
