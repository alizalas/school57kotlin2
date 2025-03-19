package school57kotlin2.demo

import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import school57kotlin2.demo.client.BlackListClient
import school57kotlin2.demo.client.SanctionsClient
import school57kotlin2.demo.controller.dto.TransferDto
import school57kotlin2.demo.controller.dto.UserDto
import school57kotlin2.demo.repository.UserRepository
import school57kotlin2.demo.service.UserService

@SpringBootTest
class MyServiceTest {

    @MockkBean
    lateinit var blackListClient: BlackListClient

    @MockkBean
    lateinit var sanctionsClient: SanctionsClient

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `когда добавляют пользователя, который отсуствует в списке террористов, он добавляется в базу`() {
        val newUser = UserDto(
            name = "Петя",
            age = 56,
            balance = 35687
        )

        every { blackListClient.searchInBlacklist(newUser.name, newUser.age) } returns listOf()

        userService.addUser(newUser)

        val user = userRepository.findByName(newUser.name)

        user shouldNotBe null
    }

    fun `когда пользователь находится в санкционном списке, ему нельзя перевести деньги`() {
        val fromUser = UserDto(
            name = "Вася",
            age = 54,
            balance = 50000
        )

        val toUser = UserDto(
            name = "Петя",
            age = 56,
            balance = 35687
        )

        userService.addUser(fromUser)
        userService.addUser(toUser)

        val user = userRepository.findByName(toUser.name)

        val userBalance = user!!.balance

        userService.transferMoney(TransferDto(fromUser.name, toUser.name, fromUser.balance))

        user.balance shouldBe userBalance

        }


}