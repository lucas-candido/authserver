import jakarta.persistence.*

@Entity
@Table(name = "TblProduct")
class Product(
        @Id @GeneratedValue
        var id: Long? = null,

        @Column(nullable = false)
        val name: String,

        @Column(nullable = false)
        val price: Double,

        @ManyToMany(mappedBy = "products")
        val orders: MutableSet<Order> = mutableSetOf()
) {

}