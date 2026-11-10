package com.example.crm;

import com.example.crm.repository.ClientRepository;
import com.example.crm.repository.CompanyRepository;
import com.example.crm.repository.ContactRepository;
import com.example.crm.repository.DealRepository;
import com.example.crm.repository.NoteRepository;
import com.example.crm.repository.ProductRepository;
import com.example.crm.repository.TaskRepository;
import com.example.crm.repository.UserRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Базовый класс тестов: запускает Spring-контекст, но все репозитории заменены Mockito-заглушками,
 * поэтому к базе данных тесты не обращаются.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class MockRepositoriesTest {

    @MockBean
    protected ClientRepository clientRepository;

    @MockBean
    protected CompanyRepository companyRepository;

    @MockBean
    protected ContactRepository contactRepository;

    @MockBean
    protected ProductRepository productRepository;

    @MockBean
    protected DealRepository dealRepository;

    @MockBean
    protected TaskRepository taskRepository;

    @MockBean
    protected NoteRepository noteRepository;

    @MockBean
    protected UserRepository userRepository;
}
