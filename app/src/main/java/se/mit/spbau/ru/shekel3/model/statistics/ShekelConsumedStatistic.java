package se.mit.spbau.ru.shekel3.model.statistics;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.mit.spbau.ru.shekel3.model.ShekelUser;

/**
 * Created by John on 12/12/2015.
 */
public class ShekelConsumedStatistic {
    ShekelUser user;
    Float consumed;
    Integer itemsCount;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Model {
        Integer user;
        Float consumed;
        Integer items_consumed;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Container {
        Integer result;
        List<Model> data;
    }
}
