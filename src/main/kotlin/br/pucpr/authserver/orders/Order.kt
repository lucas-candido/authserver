import br.pucpr.authserver.users.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "TblOrder")
class Order(
        @Id @GeneratedValue
        var id: Long? = null,

        @ManyToMany
        @JoinTable(
                name = "OrderProduct",
                joinColumns = [JoinColumn(name = "orderId")],
                inverseJoinColumns = [JoinColumn(name = "productId")]
        )
        val products: MutableSet<Product> = mutableSetOf(),

        @ManyToOne
        @JoinColumn(name = "userId")
        val user: User,

        val orderDate: LocalDateTime = LocalDateTime.now()
) {

}
