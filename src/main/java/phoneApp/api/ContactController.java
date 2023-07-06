package phoneApp.api;

import phoneApp.service.phoneBook.PhoneBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import phoneApp.service.phoneBook.ContactDto;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final PhoneBookService phoneBookService;

    @GetMapping("/{id}")
    public List<ContactDto> get(@PathVariable("id") String id) {

        return phoneBookService.getContacts(id);
    }

    @PostMapping
    public ContactDto create(@PathVariable("userId") String userId, @RequestBody ContactDto request) {
        return phoneBookService.createContact(userId, request);
    }

    @GetMapping
    public List<ContactDto> index(@PathVariable("userId") String userId) {
        return phoneBookService.getContacts(userId);
    }
    @PutMapping("/{id}")
    public ContactDto update(@PathVariable("id") String id, @RequestBody ContactDto contactDto) {
        return phoneBookService.updateContact(id, contactDto);
    }
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable("id") String id) {
        phoneBookService.deleteContact(id);
    }



}
