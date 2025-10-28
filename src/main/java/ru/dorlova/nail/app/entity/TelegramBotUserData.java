package ru.dorlova.nail.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tg-bot-user-data")
@Data
@NoArgsConstructor
public class TelegramBotUserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
}
