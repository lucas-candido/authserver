import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val productRepository: ProductsRepository) {

    @GetMapping("/")
    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<Product> {
        val product = productRepository.findById(id)
        return if (product.isPresent) {
            ResponseEntity.ok(product.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/")
    fun createProduct(@Valid @RequestBody product: Product): ResponseEntity<Product> {
        val createdProduct = productRepository.save(product)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct)
    }

    @PutMapping("/{id}")
    fun updateProduct(
            @PathVariable id: Long,
            @Valid @RequestBody updatedProduct: Product
    ): ResponseEntity<Product> {
        val product = productRepository.findById(id)
        return if (product.isPresent) {
            val updated = productRepository.save(updatedProduct)
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Unit> {
        return if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
