import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(private val orderRepository: OrdersRepository, private val productsRepository: ProductsRepository) {

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

    @GetMapping("/{id}/products")
    fun getOrderProducts(@PathVariable id: Long): Set<Product> {
        val order = orderRepository.findById(id)
        return if (order.isPresent) {
            order.get().products
        } else {
            emptySet()
        }
    }

    @PostMapping("/{id}/products/{productId}")
    fun addProductToOrder(@PathVariable id: Long, @PathVariable productId: Long): ResponseEntity<Order> {
        val order = orderRepository.findById(id)
        val product = productsRepository.findById(productId)
        return if (order.isPresent && product.isPresent) {
            order.get().products.add(product.get())
            val updatedOrder = orderRepository.save(order.get())
            ResponseEntity.ok(updatedOrder)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}/products/{productId}")
    fun removeProductFromOrder(@PathVariable id: Long, @PathVariable productId: Long): ResponseEntity<Order> {
        val order = orderRepository.findById(id)
        val product = productsRepository.findById(productId)
        return if (order.isPresent && product.isPresent) {
            order.get().products.remove(product.get())
            val updatedOrder = orderRepository.save(order.get())
            ResponseEntity.ok(updatedOrder)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
