package phoneApp.api;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;


import phoneApp.entity.Contact;
import phoneApp.entity.Email;
import phoneApp.entity.PhoneNumber;
import phoneApp.repo.ContactRepository;
import phoneApp.repo.EmailRepository;
import phoneApp.repo.PhoneNumberRepository;
import phoneApp.service.phoneBook.ContactDto;
import phoneApp.service.phoneBook.PhoneBookService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PhoneBookServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private PhoneNumberRepository phoneNumberRepository;

    private PhoneBookService phoneBookService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        phoneBookService = new PhoneBookService(contactRepository,
                emailRepository, phoneNumberRepository);
    }

    @Test
    public void testUpdateContact() {
        var cod = UUID.randomUUID().toString();
        Contact existingContact = new Contact();
        existingContact.setUid(cod);
        existingContact.setPhoneNumbers(List.of(PhoneNumber.builder()
                .phoneNumber("380500812345")
                .build()));
        existingContact.setEmails(List.of(Email.builder()
                .email("example@net")
                .build()));
        ContactDto contactDto = new ContactDto(
                cod,
                "Ivan",
                "Kot",
                List.of("example@net"),
                List.of("380500812345")
        );
        when(contactRepository.findByUid(cod)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContactDto updatedContact = phoneBookService.updateContact(cod, contactDto);
        assertEquals("380500812345", updatedContact.phoneNumbers().get(0));
    }

    @Test
    public void testCreateContact() {
        var cod = UUID.randomUUID().toString();

        var request = new ContactDto(
                null,
                "Ivan",
                "Kot",
                List.of("example@net"),
                List.of("380500812345")
        );
        Contact savedContact = new Contact();
        savedContact.setUid(cod);
        savedContact.setFirstName("Ivan");
        savedContact.setLastName("Kot");
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        ContactDto createdContact = phoneBookService.createContact(cod, request);

        assertEquals(cod, createdContact.id());
        assertEquals("Ivan", createdContact.firstName());
        assertEquals("Kot", createdContact.lastName());
    }

    @Test
    void shouldThrowExceptionForInvalidCreating() {
        var userId = "123";
        var request = new ContactDto(
                userId,
                "Ivan",
                "Kot",
                List.of("vita123@ukr.net"),
                List.of("wrgdf"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                phoneBookService.createContact(userId, request));
        assertEquals("Invalid or duplicate phone number: wrgdf", exception.getMessage());

    }

    @Test
    void shouldThrowExceptionForInvalidUpdating() {

        var cod = UUID.randomUUID().toString();
        ContactDto contactDto = new ContactDto(
                cod,
                "Ivan",
                "Kot",
                List.of("Vita123@ukr.net"),
                List.of("ewfffffd")
        );
        Contact existingContact = new Contact();
        existingContact.setUid(cod);
        existingContact.setPhoneNumbers(List.of(PhoneNumber.builder()
                .phoneNumber("380500812345")
                .build()));
        existingContact.setEmails(List.of(Email.builder()
                .email("example@net")
                .build()));

        when(contactRepository.findByUid(cod)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                phoneBookService.updateContact(cod, contactDto));
        assertEquals("Invalid phone number: ewfffffd", exception.getMessage());
    }


}
