package br.pucpr.authserver.orders

import br.pucpr.authserver.orders.requests.OrdersRequest
import br.pucpr.authserver.services.Service
import br.pucpr.authserver.services.ServicesRepository
import br.pucpr.authserver.users.UsersRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/orders")
@Tag(name = "APIs de Pedidos")
class OrdersController(
    private val orderRepository: OrdersRepository,
    private val servicesRepository: ServicesRepository,
    private val usersRepository: UsersRepository
) {

    @GetMapping("/")
    @SecurityRequirement(name = "AuthServer")
    @Operation(
            summary = "Lista todos os pedidos",
            description = "Retorna uma lista com todos os pedidos existentes",
            parameters = [
                Parameter(
                        name = "userId",
                        description = "Usuário a ser usado no filtro (opcional)"
                ),
                Parameter(
                        name = "order",
                        description = "Tipo de ordenação pelo ID do pedido (opcional). Padrão ASC ou informe a string DESC"
                )
            ]
    )
    fun getAllOrders(@RequestParam("userId") userId: Long?, @RequestParam("order") order: String?): List<Order> {

        var orders = orderRepository.findAll()
        orders = if (order.equals("DESC")) orders.sortedByDescending { it.id } else orders.sortedBy { it.id }

        return if (userId != null) orders.filter {  it.user.id == userId } else orders
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
    fun createOrder(@Valid @RequestBody req: OrdersRequest): ResponseEntity<String> {
        val user = usersRepository.findById(req.userId!!)
        val service = servicesRepository.findById(req.serviceId!!)
        val services = mutableSetOf<Service>()

        return if (user.isPresent && service.isPresent) {
            services.add(service.get())
            val order = Order(user = user.get(), services = services, orderDate = LocalDateTime.now())
            val createdOrder = orderRepository.save(order)
            ResponseEntity.status(HttpStatus.CREATED).body(createdOrder.toString())
        } else {
            if (!user.isPresent) ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado") else
                if (!service.isPresent) ResponseEntity.status(HttpStatus.NOT_FOUND).body("Serviço não encontrado") else
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao processar o corpo da requisição")
        }
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VENDAS')")
    @Operation(summary = "Atualiza um pedido", description = "Retorna o pedido atualizado")

    fun updateOrder(
        @PathVariable id: Long,
        @Valid @RequestBody newUserId: Long
    ): ResponseEntity<String> {
        val order = orderRepository.findById(id)
        val user = usersRepository.findById(newUserId)
        return if (order.isPresent && user.isPresent) {
            val updated = orderRepository.save(order.get().copy(user = user.get()))
            ResponseEntity.ok(updated.toString())
        } else {
            if (!user.isPresent) ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado") else
                if (!order.isPresent) ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido não encontrado") else
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao processar o corpo da requisição")
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
