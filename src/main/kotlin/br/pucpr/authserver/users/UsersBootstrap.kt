package br.pucpr.authserver.users

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class UsersBootstrap(
    val rolesRepository: RolesRepository,
    val userRepository: UsersRepository
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = Role(name = "ADMIN")
        val registrationRole = Role(name = "CADASTRO")
        val salesRole = Role(name = "VENDAS")
        val customerRole = Role(name = "CLIENTE")

        if (rolesRepository.count() == 0L) {
            rolesRepository.save(adminRole)
            rolesRepository.save(registrationRole)
            rolesRepository.save(salesRole)
            rolesRepository.save(customerRole)
        }
        if (userRepository.count() < 4L) {
            val admin = User(
                email = "admin@authserver.com",
                password = "admin",
                name = "Auth Server Administrator",
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)

            val registration = User(
                    email = "cadastro@authserver.com",
                    password = "cadastro",
                    name = "Usuário de Cadastro",
            )
            registration.roles.add(registrationRole)
            userRepository.save(registration)

            val sales = User(
                    email = "vendas@authserver.com",
                    password = "vendas",
                    name = "Usuário de Vendas",
            )
            sales.roles.add(salesRole)
            userRepository.save(sales)

            val customer = User(
                    email = "cliente@authserver.com",
                    password = "cliente",
                    name = "Cliente de Exemplo",
            )
            customer.roles.add(customerRole)
            userRepository.save(customer)
        }
    }
}