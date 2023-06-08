package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "items", schema = "shareit")
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    String name;
    @NotBlank
    String description;

    @NotNull
    @Column(name = "is_available")
    Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    User owner;


    @OneToOne
    @Transient
    ItemRequest request;

}
