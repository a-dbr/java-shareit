package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShareItServerTest {

    @Test
    void contextLoads() {
        // Тест проверяет, что контекст Spring приложения загружается корректно
    }

    @Test
    void main_shouldRunApplicationWithoutExceptions() {
        // Проверяем, что метод main запускается без исключений
        ShareItServer.main(new String[] {});
    }
}