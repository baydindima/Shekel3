package se.mit.spbau.ru.shekel3.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * ShekelItem model
 * Created by John on 11/13/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class ShekelItem implements ShekelNamed {
    private String name;
    private Integer id;
    private Integer cost;
    private List<ShekelUser> consumers = new ArrayList<>();
    private ShekelUser customer;

    /**
     * JSON model
     */
    @Getter
    @Setter
    @AllArgsConstructor(suppressConstructorProperties = true)
    public static class ItemModel {
        private String name;
        private Integer id;
        private Integer cost;
        private List<Integer> consumerIds;
        private Integer customer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemModelContainer {
        private List<ItemModel> data;
    }
}
