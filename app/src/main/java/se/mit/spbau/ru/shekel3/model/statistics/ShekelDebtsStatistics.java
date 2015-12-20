package se.mit.spbau.ru.shekel3.model.statistics;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.mit.spbau.ru.shekel3.model.ShekelUser;

/**
 * Created by John on 12/18/2015.
 */
@NoArgsConstructor
@Getter
@Setter
public class ShekelDebtsStatistics {
    private Float debt;
    private ShekelUser from;
    private ShekelUser to;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Model {
        private Float debt;
        private Integer from;
        private Integer to;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ModelContainer {
        List<Model> debts;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Container {
        Integer result;
        ModelContainer data;
    }
}
