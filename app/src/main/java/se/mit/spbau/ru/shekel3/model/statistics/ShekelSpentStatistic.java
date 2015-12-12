package se.mit.spbau.ru.shekel3.model.statistics;

import android.graphics.AvoidXfermode;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.mit.spbau.ru.shekel3.model.ShekelUser;

/**
 * Created by John on 12/12/2015.
 */
@NoArgsConstructor
@Getter
@Setter
public class ShekelSpentStatistic {
    ShekelUser user;
    Integer spent;
    Integer itemsCount;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Model {
        Integer user;
        Integer spent;
        Integer items_bought;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Container {
        Integer result;
        List<Model> data;
    }
}
