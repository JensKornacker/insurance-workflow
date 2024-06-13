package at.phactum.demo.insurance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.phactum.demo.shared.utils.DataItemConverter;
import at.phactum.demo.shared.utils.DataItem;
import at.phactum.demo.shared.utils.DataItemGroup;
import at.phactum.demo.shared.utils.HashMapConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InsuranceApplicationTests {

    @Autowired
    private DataItemConverter dataItemConverter;

    @Autowired
    private HashMapConverter hashMapConverter;

    @Test
    void contextLoads() {
    }

    @Test
    void testAttributeConverter() {

        DataItemGroup account = new DataItemGroup("account");
        account.add(new DataItem<>("monthly", "5000.-")).add(new DataItem<>("yearly", "70000.-"));

        DataItemGroup customer = new DataItemGroup("customer");
        customer.add(new DataItem<>("name", "Klara Klabauter"))
                .add(new DataItem<>("email", "klara@kobold.at"))
                .add(new DataItem<>("telephone", "+4356778899"))
                .add(new DataItem<>("birthday", "2024-12-12"))
                .add(new DataItem<>("gender", "MALE"))
                .add(new DataItem<>("street", "Straße 1/1"))
                .add(new DataItem<>("zipCode", "1200"))
                .add(new DataItem<>("city", "Wien"))
                .add(new DataItem<>("country", "AT"));

        List<DataItemGroup> dataItemGroupList = new ArrayList<>();
        dataItemGroupList.add(customer);
        dataItemGroupList.add(account);
        final String converted = dataItemConverter.convertToDatabaseColumn(dataItemGroupList);
        final List<DataItemGroup> backToDataItemGroupList = dataItemConverter.convertToEntityAttribute(converted);
        assertThat(backToDataItemGroupList.equals(dataItemGroupList));

        Map<String, Object> config = new HashMap<>();

        config.put("gender", "DIVERS");
        config.put("monthlyIncome", 1000);
        config.put("worthy", "REJECTED");

        final String configString = hashMapConverter.convertToDatabaseColumn(config);
        assertThat(configString.contains("worthy"));
        final Map<String, Object> configMap = hashMapConverter.convertToEntityAttribute(configString);
        assertThat(configMap.equals(config));
    }


}
