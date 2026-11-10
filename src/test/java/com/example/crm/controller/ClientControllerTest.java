package com.example.crm.controller;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.model.Client;
import com.example.crm.model.ClientType;
import com.example.crm.model.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClientControllerTest extends MockRepositoriesTest {

    @Autowired
    private MockMvc mockMvc;

    private Client client(long id, String name) {
        Client client = new Client();
        client.setId(id);
        client.setName(name);
        client.setEmail(name.toLowerCase() + "@mail.ru");
        return client;
    }

    @Test
    void getAllReturnsClients() throws Exception {
        when(clientRepository.search("", null, null)).thenReturn(List.of(client(1L, "Ivan"), client(2L, "Anna")));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ivan"));
    }

    @Test
    void getAllPassesFiltersToRepository() throws Exception {
        when(clientRepository.search("an", ClientType.COMPANY, 3L)).thenReturn(List.of(client(2L, "Anna")));

        mockMvc.perform(get("/api/clients?name=an&type=COMPANY&companyId=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Anna"));
    }

    @Test
    void wrongTypeInFilterGives400() throws Exception {
        mockMvc.perform(get("/api/clients?type=WRONG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdReturnsClient() throws Exception {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client(1L, "Ivan")));

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"));
    }

    @Test
    void getByIdReturns404() throws Exception {
        when(clientRepository.findById(5L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createClientReturns201() throws Exception {
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@mail.ru\",\"name\":\"New\",\"phone\":\"+7(900)123-45-67\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@mail.ru"))
                .andExpect(jsonPath("$.clientType").value("INDIVIDUAL"));
    }

    @Test
    void createClientWithWrongEmailReturns400() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"wrong\",\"name\":\"New\"}"))
                .andExpect(status().isBadRequest());

        verify(clientRepository, never()).save(any());
    }

    @Test
    void createClientWithUnknownCompanyReturns400() throws Exception {
        when(companyRepository.findById(9L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@mail.ru\",\"name\":\"New\",\"companyId\":9}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClientWithCompany() throws Exception {
        Company company = new Company("Romashka", "Moscow");
        company.setId(9L);
        when(companyRepository.findById(9L)).thenReturn(Optional.of(company));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@mail.ru\",\"name\":\"New\",\"companyId\":9}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.company.name").value("Romashka"));
    }

    @Test
    void updateMissingClientReturns404() throws Exception {
        when(clientRepository.existsById(5L)).thenReturn(false);

        mockMvc.perform(put("/api/clients/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@mail.ru\",\"name\":\"New\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateClientReturns200() throws Exception {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client(1L, "Ivan")));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ivan@mail.ru\",\"name\":\"Ivan Petrov\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ivan Petrov"));
    }

    @Test
    void deleteClientReturns204() throws Exception {
        when(clientRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());

        verify(clientRepository).deleteById(1L);
    }

    @Test
    void deleteMissingClientReturns404() throws Exception {
        when(clientRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNotFound());
    }
}
