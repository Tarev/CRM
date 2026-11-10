package com.example.crm.controller;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.model.Company;
import com.example.crm.model.Contact;
import com.example.crm.model.Deal;
import com.example.crm.model.Note;
import com.example.crm.model.user.AdminUser;
import com.example.crm.model.user.ManagerUser;
import com.example.crm.model.user.User;
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

class UserContactNoteControllerTest extends MockRepositoriesTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsersShowsRoles() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(new AdminUser("admin", "Admin"), new ManagerUser("m", "Manager")));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].role").value("MANAGER"));
    }

    @Test
    void getUsersByRole() throws Exception {
        when(userRepository.findByRole("MANAGER")).thenReturn(List.of(new ManagerUser("m", "Manager")));

        mockMvc.perform(get("/api/users?role=MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].login").value("m"));
    }

    @Test
    void createAdminReturns201() throws Exception {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"fullName\":\"Admin\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void createUserWithoutRoleReturns400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"fullName\":\"Admin\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAdminWithClientReturns400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"fullName\":\"Admin\",\"role\":\"ADMIN\",\"clientId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMissingUserReturns404() throws Exception {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/7"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getContactsByCompany() throws Exception {
        Contact contact = new Contact();
        contact.setFullName("Maria");
        when(contactRepository.findByCompanyId(3L)).thenReturn(List.of(contact));

        mockMvc.perform(get("/api/contacts?companyId=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Maria"));
    }

    @Test
    void createContactReturns201() throws Exception {
        when(companyRepository.findById(3L)).thenReturn(Optional.of(new Company("Romashka", "Moscow")));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"c@mail.ru\",\"fullName\":\"Maria\",\"companyId\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.company.name").value("Romashka"));
    }

    @Test
    void createContactWithoutCompanyReturns400() throws Exception {
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"c@mail.ru\",\"fullName\":\"Maria\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNotesByDeal() throws Exception {
        Note note = new Note(new Deal(), "first note");
        when(noteRepository.findByDealIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(note));

        mockMvc.perform(get("/api/notes?dealId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("first note"));
    }

    @Test
    void createNoteReturns201() throws Exception {
        when(dealRepository.findById(1L)).thenReturn(Optional.of(new Deal()));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dealId\":1,\"text\":\"hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("hello"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void createNoteWithEmptyTextReturns400() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dealId\":1,\"text\":\" \"}"))
                .andExpect(status().isBadRequest());
    }
}
