package com.example.crm.service;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.dto.ClientRequest;
import com.example.crm.dto.ContactRequest;
import com.example.crm.model.Client;
import com.example.crm.model.ClientType;
import com.example.crm.model.Company;
import com.example.crm.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientAndContactServiceTest extends MockRepositoriesTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ContactService contactService;

    private ClientRequest clientRequest(Long companyId) {
        ClientRequest request = new ClientRequest();
        request.setEmail("ivan@mail.ru");
        request.setName("Ivan");
        request.setCompanyId(companyId);
        return request;
    }

    private Company company(Long id) {
        Company company = new Company("Romashka", "Moscow");
        company.setId(id);
        return company;
    }

    @Test
    void createClientWithCompany() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Client> result = clientService.create(clientRequest(1L));

        assertTrue(result.isPresent());
        assertEquals("Romashka", result.get().getCompany().getName());
        assertEquals(ClientType.INDIVIDUAL, result.get().getClientType());
    }

    @Test
    void createClientWithoutCompany() {
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Client> result = clientService.create(clientRequest(null));

        assertTrue(result.isPresent());
        assertNull(result.get().getCompany());
    }

    @Test
    void createClientWithUnknownCompanyReturnsEmpty() {
        when(companyRepository.findById(9L)).thenReturn(Optional.empty());

        assertTrue(clientService.create(clientRequest(9L)).isEmpty());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateMissingClientReturnsEmpty() {
        when(clientRepository.findById(5L)).thenReturn(Optional.empty());

        assertTrue(clientService.update(5L, clientRequest(null)).isEmpty());
    }

    @Test
    void updateClientChangesFieldsAndKeepsType() {
        Client client = new Client();
        client.setId(5L);
        client.setClientType(ClientType.COMPANY);
        when(clientRepository.findById(5L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Client> result = clientService.update(5L, clientRequest(null));

        assertEquals("Ivan", result.get().getName());
        assertEquals(ClientType.COMPANY, result.get().getClientType());
    }

    @Test
    void searchReplacesNullNameWithEmptyString() {
        Client client = new Client();
        when(clientRepository.search("", null, null)).thenReturn(List.of(client));

        assertEquals(1, clientService.search(null, null, null).size());
    }

    @Test
    void searchTrimsName() {
        when(clientRepository.search("ivan", ClientType.COMPANY, 2L)).thenReturn(List.of(new Client()));

        assertEquals(1, clientService.search("  ivan ", ClientType.COMPANY, 2L).size());
    }

    @Test
    void createContactRequiresExistingCompany() {
        ContactRequest request = new ContactRequest();
        request.setEmail("c@mail.ru");
        request.setFullName("Maria");
        request.setCompanyId(3L);
        when(companyRepository.findById(3L)).thenReturn(Optional.empty());

        assertTrue(contactService.create(request).isEmpty());
    }

    @Test
    void createContactSavesIt() {
        ContactRequest request = new ContactRequest();
        request.setEmail("c@mail.ru");
        request.setFullName("Maria");
        request.setPosition("Director");
        request.setCompanyId(3L);
        when(companyRepository.findById(3L)).thenReturn(Optional.of(company(3L)));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Contact> result = contactService.create(request);

        assertEquals("Director", result.get().getPosition());
        assertEquals(3L, result.get().getCompany().getId());
    }

    @Test
    void findContactsWithoutCompanyIdReturnsAll() {
        when(contactRepository.findAll()).thenReturn(List.of(new Contact(), new Contact()));

        assertEquals(2, contactService.findByCompanyId(null).size());
    }

    @Test
    void findContactsByCompany() {
        when(contactRepository.findByCompanyId(3L)).thenReturn(List.of(new Contact()));

        assertEquals(1, contactService.findByCompanyId(3L).size());
    }
}
