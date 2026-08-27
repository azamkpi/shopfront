package uz.azam.shopfront.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.bot")
@Getter
@Setter
public class BotProperties {

    private String token;
    private String username;

}
