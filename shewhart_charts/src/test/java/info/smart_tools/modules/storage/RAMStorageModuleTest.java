package info.smart_tools.modules.storage;

import org.junit.Before;
import org.junit.Test;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;
import info.smart_tools.shewhart_charts.modules.storage.RAMStorageModule;
import info.smart_tools.shewhart_charts.modules.storage.StorageModule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class RAMStorageModuleTest {
    private StorageModule<Date, Double> storageModule;
    private DateFormat dateFormat;
    private List<ChartControlGroup<Date, Double>> controlGroups;


    @Before
    public void setUp() throws ParseException {
        storageModule = RAMStorageModule.create();
        dateFormat = new SimpleDateFormat();

        controlGroups = new LinkedList<>();
        ChartControlGroup<Date, Double> firstGroup = mock(ChartControlGroup.class);
        ChartControlGroup<Date, Double> secondGroup = mock(ChartControlGroup.class);
        ChartControlGroup<Date, Double> thirdGroup = mock(ChartControlGroup.class);
        ChartControlGroup<Date, Double> fourthGroup = mock(ChartControlGroup.class);
        ChartControlGroup<Date, Double> fiveGroup = mock(ChartControlGroup.class);

        controlGroups.add(fourthGroup);
        controlGroups.add(firstGroup);
        controlGroups.add(fiveGroup);
        controlGroups.add(thirdGroup);
        controlGroups.add(secondGroup);

        DateFormat dateFormat = new SimpleDateFormat();
        when(firstGroup.getKey()).thenReturn(dateFormat.parse("01/01/2016 01:01 AM"));
        when(secondGroup.getKey()).thenReturn(dateFormat.parse("02/02/2016 02:02 AM"));
        when(thirdGroup.getKey()).thenReturn(dateFormat.parse("03/03/2016 03:03 AM"));
        when(fourthGroup.getKey()).thenReturn(dateFormat.parse("04/04/2016 04:04 AM"));
        when(fiveGroup.getKey()).thenReturn(dateFormat.parse("05/05/2016 05:05 AM"));

        storageModule.save(controlGroups);
    }

    @Test
    public void getControlGroupsByBeginKey() throws ParseException {
        Date beginKey = dateFormat.parse("03/03/2016 03:03 AM");
        List<ChartControlGroup<Date, Double>> groups = storageModule.get(beginKey);
        assertEquals(groups.size(), 3);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void getControlGroupsByBeginAndEndKeys() throws ParseException {
        Date beginKey = dateFormat.parse("02/02/2016 02:02 AM");
        Date endKey = dateFormat.parse("04/04/2016 04:04 AM");
        List<ChartControlGroup<Date, Double>> groups = storageModule.get(beginKey, endKey);
        assertEquals(groups.size(), 3);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
    }

    @Test
    public void getAllControlGroups() throws ParseException {
        List<ChartControlGroup<Date, Double>> groups = storageModule.getAll();
        assertEquals(groups.size(), 5);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(3).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(4).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));

    }

    @Test
    public void removeControlGroupsByKey() throws ParseException {
        StorageModule<Date, Double> storage = RAMStorageModule.create();
        storage.save(controlGroups);

        Date key = dateFormat.parse("03/03/2016 03:03 AM");
        storage.remove(key);
        List<ChartControlGroup<Date, Double>> groups = storage.getAll();
        assertEquals(groups.size(), 4);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(3).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void removeControlGroupsByBeginAndEndKeys() throws ParseException {
        StorageModule<Date, Double> storage = RAMStorageModule.create();
        storage.save(controlGroups);
        Date beginKey = dateFormat.parse("02/02/2016 02:02 AM");
        Date endKey = dateFormat.parse("04/04/2016 04:04 AM");
        storage.remove(beginKey, endKey);
        List<ChartControlGroup<Date, Double>> groups = storage.getAll();
        assertEquals(groups.size(), 2);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void removeAllControlGroups() {
        StorageModule<Date, Double> storage = RAMStorageModule.create();
        storage.save(controlGroups);
        storage.removeAll();
        assertEquals(storage.size(), 0);
    }

}
