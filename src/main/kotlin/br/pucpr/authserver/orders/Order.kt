package br.pucpr.authserver.orders

import br.pucpr.authserver.services.Service
import br.pucpr.authserver.users.User
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "TblOrder")
data class Order(

    @Id @GeneratedValue
    var id: Long? = null,

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "OrderProduct",
        joinColumns = [JoinColumn(name = "orderId")],
        inverseJoinColumns = [JoinColumn(name = "productId")]
    )
    val services: MutableSet<Service> = mutableSetOf(),

    @ManyToOne
    @JoinColumn(name = "userId")
    val user: User,

    val orderDate: LocalDateTime = LocalDateTime.now()
) {
    override fun toString(): String = "Pedido de id $id criado/atualizado em $orderDate para o usuário ${user.name}"
}


