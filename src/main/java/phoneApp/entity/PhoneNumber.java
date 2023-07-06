package phoneApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "phone_numbers")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PhoneNumber extends BaseEntity {

    private String phoneNumber;


    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id", nullable = false)
    private Contact contact;
}
