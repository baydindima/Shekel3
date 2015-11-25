package se.mit.spbau.ru.shekel3.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by John on 11/24/2015.
 */

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class ShekelReceipt implements ShekelBaseEntity {
    private Integer id;
    private String name;
    private Integer cost;
    private ShekelUser owner;
    private List<ShekelItem> items;
    private List<ShekelUser> consumerIds;

    /**
     * JSON model
     */
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor(suppressConstructorProperties = true)
    private static class ShekelReceiptModel {
        private Integer id;
        private String name;
        private Integer cost;
        private Integer owner;
        private List<ShekelItem.ItemModel> items;
        private List<Integer> consumer_ids;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor(suppressConstructorProperties = true)
    private static class ShekelReceiptModelContainer {
        private List<ShekelReceiptModel> data;
        private Integer result;
    }
}
