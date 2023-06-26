package br.pucpr.authserver.orders

import br.pucpr.authserver.services.Service
import br.pucpr.authserver.services.ServicesRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrdersController(
    private val orderRepository: OrdersRepository,
    private val servicesRepository: ServicesRepository
) {

    @GetMapping("/")
    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): ResponseEntity<Order> {
        val order = orderRepository.findById(id)
        return if (order.isPresent) {
            ResponseEntity.ok(order.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/")
    fun createOrder(@Valid @RequestBody order: Order): ResponseEntity<Order> {
        val createdOrder = orderRepository.save(order)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder)
    }

    @PutMapping("/{id}")
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
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<Unit> {
        return if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/services")
    fun getOrderServices(@PathVariable id: Long): Set<Service> {
        val order = orderRepository.findById(id)
        return if (order.isPresent) {
            order.get().services
        } else {
            emptySet()
        }
    }

    @PostMapping("/{id}/services/{serviceId}")
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
