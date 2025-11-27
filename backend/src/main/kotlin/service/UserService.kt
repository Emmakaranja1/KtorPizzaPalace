package com.emmascode.services

import com.emmascode.repository.UserRepository
import com.emmascode.dto.*
import com.emmascode.utils.HashUtils
import com.emmascode.utils.JwtTokenProvider

class UserService(private val repository: UserRepository = UserRepository()) {
    fun getAllUsers() = repository.findAll()
    fun getUserById(id: Int) = repository.findById(id)
    fun getUserByEmail(email: String) = repository.findByEmail(email)

    fun createUser(dto: CreateUserDTO): UserDTO {
        val hashedPassword = HashUtils.hashPassword(dto.password)
        return repository.create(dto.username, dto.email, hashedPassword, dto.role)
    }

    fun updateUser(id: Int, dto: UpdateUserDTO): UserDTO? {
        val hashedPassword = dto.password?.let { HashUtils.hashPassword(it) }
        return repository.update(id, dto.username, dto.email, hashedPassword, dto.role)
    }

    fun deleteUser(id: Int) = repository.delete(id)

    fun login(dto: LoginDTO): LoginResponseDTO? {
        val user = repository.findByEmail(dto.email) ?: return null
        val passwordHash = repository.getPasswordHash(dto.email) ?: return null

        if (!HashUtils.verifyPassword(dto.password, passwordHash)) {
            return null
        }

        val token = JwtTokenProvider.generateToken(user.id, user.email, user.role)
        return LoginResponseDTO(token, user)
    }
}



