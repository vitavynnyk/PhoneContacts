package phoneApp.service.phoneBook;

import phoneApp.entity.Contact;
import phoneApp.entity.Email;
import phoneApp.entity.PhoneNumber;
import phoneApp.repo.ContactRepository;
import phoneApp.repo.EmailRepository;
import phoneApp.repo.PhoneNumberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhoneBookService {
    private final ContactRepository contactRepository;
    private final EmailRepository emailRepository;
    private final PhoneNumberRepository phoneNumberRepository;

    @Transactional
    public ContactDto createContact(String userId, ContactDto request) {

        var contact = contactRepository.save(Contact.builder()
                .uid(UUID.randomUUID().toString())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .userId(userId)
                .build());
        validateEmails(request.emails());
        validatePhoneNumbers(request.phoneNumbers());
        var emails = emailRepository.saveAll(request.emails().stream()
                .map(value -> Email.builder()
                        .email(value)
                        .contact(contact)
                        .build())
                .toList());
        contact.setEmails(emails);

        var phoneNumbers = phoneNumberRepository.saveAll(request.phoneNumbers().stream()
                .map(value -> PhoneNumber.builder()
                        .phoneNumber(value)
                        .contact(contact)
                        .build())
                .toList());
        contact.setPhoneNumbers(phoneNumbers);

        return map(contact);
    }

    private void validateEmails(List<String> emails) {
        Set<String> uniqueEmails = new HashSet<>();
        for (String email : emails) {
            if (!uniqueEmails.add(email)) {
                throw new IllegalArgumentException("Duplicate email: " + email);
            }
            if (isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email: " + email);
            }
        }
    }

    private void validatePhoneNumbers(List<String> phoneNumbers) {
        Set<String> uniquePhoneNumbers = new HashSet<>();
        for (String phoneNumber : phoneNumbers) {
            if (isValidPhoneNumber(phoneNumber) || !uniquePhoneNumbers.add(phoneNumber)) {
                throw new IllegalArgumentException("Invalid or duplicate phone number: " + phoneNumber);
            }
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        return email == null || !email.matches(regex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^\\+?380\\d{9}$";
        return phoneNumber == null || !phoneNumber.matches(regex);
    }

    @Transactional
    public List<ContactDto> getContacts(String userId) {
        return contactRepository.findByUserId(userId).stream()
                .map(this::map)
                .toList();
    }

    @Transactional
    public ContactDto updateContact(String uid, ContactDto contactDto) {
        var contactToUpdate = contactRepository.findByUid(uid).orElseThrow();
        if (contactDto.phoneNumbers() != null) {
            for (String phoneNumber : contactDto.phoneNumbers()) {
                validatePhoneNumber(phoneNumber);
                for (PhoneNumber phoneNumberOfContact : contactToUpdate.getPhoneNumbers()) {
                    phoneNumberOfContact.setPhoneNumber(phoneNumber);
                }
            }
        }
        if (contactDto.emails() != null) {
            for (String email : contactDto.emails()) {
                validateEmail(email);
                for (Email emailOfContact : contactToUpdate.getEmails()) {
                    emailOfContact.setEmail(email);
                }
            }
        }
        return map(contactRepository.save(contactToUpdate));
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number: " + phoneNumber);
        }
    }

    private void validateEmail(String email) {
        if (isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
    }

    @Transactional
    public void deleteContact(String uid) {
        var contact = contactRepository.findByUid(uid).orElseThrow();
        contactRepository.delete(contact);
    }

    private ContactDto map(Contact contact) {
        return new ContactDto(
                contact.getUid(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmails().stream().map(Email::getEmail).toList(),
                contact.getPhoneNumbers().stream().map(PhoneNumber::getPhoneNumber).toList()
        );
    }
}
