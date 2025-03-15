package school57kotlin2.demo.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import school57kotlin2.demo.client.BlackListClient
import school57kotlin2.demo.controller.dto.UserDto
import school57kotlin2.demo.controller.dto.toEntity
import school57kotlin2.demo.repository.UserRepository


class UserServiceTest {
// 1-й вариант
//    private val blackListClient: mockk<BlackListClient>()
//    private val userRepository: mockk<UserRepository>()
//    private val userService: UserService(userRepository, blackListClient)
// 2-й вариант
//    @MockkBean
//    lateinit var blackListClient: BlackListClient
//
//    @Autowired
//    lateinit var userRepository: UserRepository
//
//    @Autowired
//    lateinit var userService: UserService

    private val blackListClient = mockk<BlackListClient>()
    private val userRepository = mockk<UserRepository>()
    private val userService = UserService(userRepository, blackListClient)

    @Test
    fun `когда добавляют пользователя, который отсуствует в списке террористов, он добавляется в базу`() {
        val newUser = UserDto(
            name = "Петя",
            age = 56,
            balance = 35687
        )

        every { blackListClient.searchInBlacklist(newUser.name, newUser.age) } returns listOf()
        every { userRepository.save(any()) } answers { firstArg() }

        val savedUser = userService.addUser(newUser)

        savedUser shouldBe newUser
    }

    @Test
    fun `когда добавляют пользователя, который есть в списке террористов, он не добавляется в базу`() {
        val newUser = UserDto(
            name = "Петя",
            age = 56,
            balance = 35687
        )

        every {blackListClient.searchInBlacklist(newUser.name, newUser.age)} returns listOf("Террорист")
        every {userRepository.save(any())} answers {firstArg()}

        userService.addUser(newUser)

        verify(exactly = 0) {userRepository.save(newUser.toEntity())}

    }

    @Test
    fun `когда получают пользователя, который есть в списке террористов, он не добавляется в базу`() {
        val newUser = UserDto(
            name = "Петя",
            age = 56,
            balance = 35687
        )
        userService.addUser(newUser)

    }


}