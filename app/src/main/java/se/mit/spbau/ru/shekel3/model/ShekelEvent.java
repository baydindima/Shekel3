package se.mit.spbau.ru.shekel3.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor(suppressConstructorProperties = true)
public class ShekelEvent implements ShekelBaseEntity {
    private String name;
    private Integer id;
    private Date date;
    List<ShekelUser> users = new ArrayList<>();

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ShekelEventModel {
        private String name;
        private Integer id;
        private String date;
        List<Integer> member_ids;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ShekelEventModelContainer {
        private Integer result;
        private List<ShekelEventModel> data;
    }

}
