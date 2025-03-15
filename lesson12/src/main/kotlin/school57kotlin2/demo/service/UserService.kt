package school57kotlin2.demo.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import school57kotlin2.demo.client.BlackListClient
import school57kotlin2.demo.controller.dto.TransferDto
import school57kotlin2.demo.controller.dto.UserDto
import school57kotlin2.demo.controller.dto.toEntity
import school57kotlin2.demo.entity.toDto
import school57kotlin2.demo.repository.UserRepository

@Service
class UserService(
    val userRepository: UserRepository,
    val blackListClient: BlackListClient,
) {

    fun addUser(user: UserDto) : UserDto?{
        val blackListReason = blackListClient.searchInBlacklist(
            name = user.name,
            age = user.age
        )

        if (blackListReason.contains("Террорист")) {
            return null
        }

        val savedUser = userRepository.save(user.toEntity())
        return savedUser.toDto()
    }

    fun getUser(name: String) =
        userRepository.findByName(name) ?: throw UserNotFoundException(name)

    // @Transactional
    fun transferMoney(transferDto: TransferDto) {
        val fromUser = getUser(transferDto.from)
        val toUser = getUser(transferDto.to)

        toUser.balance += transferDto.amount
        userRepository.save(toUser)

        if (fromUser.balance < transferDto.amount) {
            throw InsufficientFundsException(fromUser.name)
        }

        fromUser.balance -= transferDto.amount
        userRepository.save(fromUser)
    }

}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InsufficientFundsException(name: String) :
    RuntimeException("Недостаточно средств для перевода y пользователя $name")

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException(name: String) : RuntimeException("User $name not found in DB")