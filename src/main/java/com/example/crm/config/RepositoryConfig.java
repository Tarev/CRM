package com.example.crm.config;

import com.example.crm.model.Client;
import com.example.crm.model.Company;
import com.example.crm.model.Contact;
import com.example.crm.model.Deal;
import com.example.crm.model.Note;
import com.example.crm.model.Product;
import com.example.crm.model.Task;
import com.example.crm.model.user.User;
import com.example.crm.repository.EntityRepository;
import com.example.crm.repository.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Создает по одному хранилищу на каждый тип сущности.
 */
@Configuration
public class RepositoryConfig {

    @Bean
    public EntityRepository<Client> clientRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public EntityRepository<Company> companyRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public EntityRepository<Contact> contactRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public EntityRepository<Product> productRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public EntityRepository<Deal> dealRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public EntityRepository<Task> taskRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public EntityRepository<Note> noteRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public EntityRepository<User> userRepository() {
        return new InMemoryRepository<>();
    }
}
