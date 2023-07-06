package phoneApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "emails")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Email extends BaseEntity {

    private String email;


    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id", nullable = false)
    private Contact contact;

}
