package br.pucpr.authserver.users

import br.pucpr.authserver.users.requests.LoginRequest
import br.pucpr.authserver.users.requests.UserRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Tag(name = "APIs de Usuários")
class UsersController(val service: UsersService) {
    @Operation(
        summary = "Lista todos os usuários",
        parameters = [
            Parameter(
                name = "role",
                description = "Papel a ser usado no filtro (opcional)"
            )]
    )
    @GetMapping
    fun listUsers(@RequestParam("role") role: String?) =
        service.findAll(role)
            .map { it.toResponse() }

    @Transactional
    @PostMapping
    @Operation(summary = "Cria um usuário", description = "Retorna o usuário criado")
    fun createUser(@Valid @RequestBody req: UserRequest) =
        service.save(req)
            .toResponse()
            .let { ResponseEntity.status(CREATED).body(it) }

    @GetMapping("/me")
    @PreAuthorize("permitAll()")
    @SecurityRequirement(name = "AuthServer")
    @Operation(summary = "Lista seu próprio usuário", description = "Retorna o usuário logado atualmente")
    fun getSelf(auth: Authentication) = getUser(auth.credentials as Long)

    @GetMapping("/{id}")
    @Operation(summary = "Lista um usuário específico", description = "Retorna o usuário informado via ID")
    fun getUser(@PathVariable("id") id: Long) =
        service.getById(id)
            ?.let { ResponseEntity.ok(it.toResponse()) }
            ?: ResponseEntity.notFound().build()

    @PostMapping("/login")
    @Operation(summary = "Efetua o login do usuário", description = "Retorna o token e informações do usuário logado")
    fun login(@Valid @RequestBody credentials: LoginRequest) =
        service.login(credentials)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "AuthServer")
    @Operation(summary = "Deleta um usuário", description = "Retorna código 200 em caso de sucesso")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> =
        if (service.delete(id)) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()
}