package br.pucpr.authserver.orders

import br.pucpr.authserver.services.Service
import br.pucpr.authserver.services.ServicesRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
@Tag(name = "APIs de Pedidos")
class OrdersController(
    private val orderRepository: OrdersRepository,
    private val servicesRepository: ServicesRepository
) {

    @GetMapping("/")
    @SecurityRequirement(name = "AuthServer")
    @Operation(summary = "Lista todos os pedidos", description = "Retorna uma lista com todos os pedidos existentes")
    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "AuthServer")
    @Operation(summary = "Lista um pedido específico", description = "Retorna um pedido baseado no ID informado na requisição")
    fun getOrder(@PathVariable id: Long): ResponseEntity<Order> {
        val order = orderRepository.findById(id)
        return if (order.isPresent) {
            ResponseEntity.ok(order.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDAS')")
    @Operation(summary = "Cria um pedido", description = "Retorna o pedido criado")
    fun createOrder(@Valid @RequestBody order: Order): ResponseEntity<Order> {
        val createdOrder = orderRepository.save(order)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder)
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDAS')")
    @Operation(summary = "Atualiza um pedido", description = "Retorna o pedido atualizado")

    fun updateOrder(
        @PathVariable id: Long,
        @Valid @RequestBody updatedOrder: Order
    ): ResponseEntity<Order> {
        val order = orderRepository.findById(id)
        return if (order.isPresent) {
            val updated = orderRepository.save(updatedOrder)
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDAS')")
    @Operation(summary = "Deleta um pedido", description = "Retorna código 200 em caso de sucesso")
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<Unit> {
        return if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/services")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDAS')")
    @Operation(summary = "Lista serviços do pedido", description = "Retorna os serviços de um pedido especificado via ID")
    fun getOrderServices(@PathVariable id: Long): Set<Service> {
        val order = orderRepository.findById(id)
        return if (order.isPresent) {
            order.get().services
        } else {
            emptySet()
        }
    }

    @PostMapping("/{id}/services/{serviceId}")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDAS')")
    @Operation(summary = "Adiciona serviço ao pedido", description = "Adiciona um serviço a um pedido especificado via ID")
    fun addServiceToOrder(@PathVariable id: Long, @PathVariable serviceId: Long): ResponseEntity<Order> {
        val order = orderRepository.findById(id)
        val service = servicesRepository.findById(serviceId)
        return if (order.isPresent && service.isPresent) {
            order.get().services.add(service.get())
            val updatedOrder = orderRepository.save(order.get())
            ResponseEntity.ok(updatedOrder)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}/services/{serviceId}")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDAS')")
    @Operation(summary = "Remove serviço do pedido", description = "Remove um serviço de um pedido especificado via ID")
    fun removeServiceFromOrder(@PathVariable id: Long, @PathVariable serviceId: Long): ResponseEntity<Order> {
        val order = orderRepository.findById(id)
        val service = servicesRepository.findById(serviceId)
        return if (order.isPresent && service.isPresent) {
            order.get().services.remove(service.get())
            val updatedOrder = orderRepository.save(order.get())
            ResponseEntity.ok(updatedOrder)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
