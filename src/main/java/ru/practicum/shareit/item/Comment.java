package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
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
    //  referencedColumnName = "id")
    private Item item;

    @OneToOne
    @JoinColumn(name = "author_id",
            referencedColumnName = "user_id")
    // referencedColumnName = "id")
    private User user;

    @Column
    private LocalDateTime created;


    public Comment() {

    }

    public Comment(Long id, String text, Item item, User user, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.user = user;
        this.created = created;
    }

    public Comment(long id, String text, Item item, User user) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.user = user;
    }
}
