package br.pucpr.authserver.users

import br.pucpr.authserver.users.responses.UserResponse
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Email

@Entity
@Table(name = "TblUser")
class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @Email
    @Column(unique = true, nullable = false)
    var email: String,

    @JsonIgnore
    @Column(length = 50)
    var password: String,

    @Column(nullable = false)
    var name: String = "",

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "UserRole",
        joinColumns = [JoinColumn(name = "idUser")],
        inverseJoinColumns = [JoinColumn(name = "idRole")]
    )
    val roles: MutableSet<Role> = mutableSetOf()
) {
    fun toResponse() = UserResponse(id!!, name, email)
}