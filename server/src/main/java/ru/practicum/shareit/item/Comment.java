package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comments", schema = "shareit")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id",
            referencedColumnName = "item_id")
    private Item item;

    @OneToOne
    @JoinColumn(name = "author_id",
            referencedColumnName = "user_id")
    private User user;

    @Column
    private LocalDateTime created;
}
