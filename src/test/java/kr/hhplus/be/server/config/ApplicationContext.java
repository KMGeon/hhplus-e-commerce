package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApplicationContext {


    @Autowired
    protected UserService userService;


    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserJpaRepository userJpaRepository;


    protected static final Long EXIST_USER = 1L;
    protected static final Long NEW_USER = 100L;

}
