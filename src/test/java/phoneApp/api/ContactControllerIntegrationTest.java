package phoneApp.api;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;


import org.springframework.test.web.servlet.MockMvc;


import phoneApp.entity.Contact;

import phoneApp.repo.ContactRepository;
import phoneApp.repo.EmailRepository;
import phoneApp.repo.PhoneNumberRepository;


import java.util.*;

import phoneApp.service.phoneBook.ContactDto;
import phoneApp.service.phoneBook.PhoneBookService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")


public class ContactControllerIntegrationTest {
    @Autowired
    protected ContactRepository contactRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    protected PhoneBookService phoneBookService;
    @Autowired
    protected EmailRepository emailRepository;
    @Autowired
    protected PhoneNumberRepository phoneNumberRepository;
    @Autowired
    protected MockMvc mockMvc;


    @Test
    void shouldCreateContact() throws Exception {
        var cod = UUID.randomUUID().toString();
        String requestBody = "{\"first_name\": \"Anna\", \"last_name\":\"Sokil\",\"emails\":[\"dfg@ukr.net\"],\"phone_numbers\":[\"+380506070834\"]}";
        mockMvc.perform(post("/api/users/{userId}/contacts", cod)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Anna"))
                .andExpect(jsonPath("$.last_name").value("Sokil"));

    }

    @Test
    void shouldGetContact() throws Exception {

        var cod = UUID.randomUUID().toString();

        var request = new ContactDto(
                cod,
                "Kate",
                "Vovk",
                List.of("kate@example.com"),
                List.of("+380967374567")
        );
        var createdContact = phoneBookService.createContact(cod, request);

        mockMvc.perform(get("/api/users/{userId}/contacts", cod))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(createdContact.id()))
                .andExpect(jsonPath("[0].first_name").value("Kate"))
                .andExpect(jsonPath("[0].last_name").value("Vovk"));
    }

    @Transactional
    @Test
    void shouldUpdateContact() throws Exception {

        var cod = UUID.randomUUID().toString();
        var request = new ContactDto(
                cod,
                "Ivan",
                "Kot",
                List.of("Ivann@example.com"),
                List.of("+380967676876")
        );
        var createdContact = phoneBookService.createContact(cod, request);

        String requestBody = "{\"phone_numbers\": [\"+380500812345\"]}";
        mockMvc.perform(put("/api/users/2/contacts/{id}", createdContact.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
        Contact updatedContact = contactRepository.findByUid(createdContact.id()).orElse(null);
        assertNotNull(updatedContact);
        entityManager.flush();
        assertEquals("+380500812345", updatedContact.getPhoneNumbers().get(0).getPhoneNumber());
    }

    @Transactional
    @Test
    void shouldDeleteContact() throws Exception {

        var cod = UUID.randomUUID().toString();
        var request = new ContactDto(
                cod,
                "Anna",
                "Tolos",
                List.of("anna@example.net"),
                List.of("380967896543")
        );
        var createdContact = phoneBookService.createContact(cod, request);
        emailRepository.deleteByContactUid(createdContact.id());
        phoneNumberRepository.deleteByContactUid(createdContact.id());
        var query = delete("/api/users/3/contacts/" + createdContact.id())
                .contentType(MediaType.APPLICATION_JSON);
        
        mockMvc.perform(query)
                .andExpect(status().isOk());
        Optional<Contact> deletedContact = contactRepository.findByUid(createdContact.id());
        assertFalse(deletedContact.isPresent());
    }

}
