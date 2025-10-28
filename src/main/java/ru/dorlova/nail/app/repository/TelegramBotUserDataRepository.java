package ru.dorlova.nail.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dorlova.nail.app.entity.TelegramBotUserData;

public interface TelegramBotUserDataRepository extends JpaRepository<TelegramBotUserData, Long> {
}
