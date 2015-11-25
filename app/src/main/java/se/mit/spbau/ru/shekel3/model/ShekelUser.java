package se.mit.spbau.ru.shekel3.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(suppressConstructorProperties = true)
@Getter
public class ShekelUser implements ShekelBaseEntity {
    private String name;
    private Integer id;

    /**
     * Uses for JSON parse
     */
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UserContainer {
        private List<ShekelUser> data;
        private Integer result;
    }
}
