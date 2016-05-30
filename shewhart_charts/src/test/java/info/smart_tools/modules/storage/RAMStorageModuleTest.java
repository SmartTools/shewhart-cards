package info.smart_tools.modules.storage;

import info.smart_tools.shewhart_charts.modules.storage.*;
import org.junit.Before;
import org.junit.Test;
import info.smart_tools.shewhart_charts.groups.ChartControlGroup;

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
    public void setUp() throws ParseException, InsertGroupsException {
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

        storageModule.insert(controlGroups);
    }

    @Test
    public void getControlGroupsByBeginKey() throws ParseException, SelectGroupsException {
        Date beginKey = dateFormat.parse("03/03/2016 03:03 AM");
        List<ChartControlGroup<Date, Double>> groups = storageModule.select(beginKey);
        assertEquals(groups.size(), 3);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void getControlGroupsByBeginAndEndKeys() throws ParseException, SelectGroupsException {
        Date beginKey = dateFormat.parse("02/02/2016 02:02 AM");
        Date endKey = dateFormat.parse("04/04/2016 04:04 AM");
        List<ChartControlGroup<Date, Double>> groups = storageModule.select(beginKey, endKey);
        assertEquals(groups.size(), 3);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
    }

    @Test
    public void getAllControlGroups() throws ParseException, SelectGroupsException {
        List<ChartControlGroup<Date, Double>> groups = storageModule.selectAll();
        assertEquals(groups.size(), 5);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(3).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(4).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));

    }

    @Test
    public void removeControlGroupsByKey()
            throws ParseException, SelectGroupsException, InsertGroupsException, DeleteGroupsException {
        StorageModule<Date, Double> storage = RAMStorageModule.create();
        storage.insert(controlGroups);

        Date key = dateFormat.parse("03/03/2016 03:03 AM");
        storage.delete(key);
        List<ChartControlGroup<Date, Double>> groups = storage.selectAll();
        assertEquals(groups.size(), 4);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(3).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void removeControlGroupsByBeginAndEndKeys()
            throws ParseException, InsertGroupsException, SelectGroupsException, DeleteGroupsException {
        StorageModule<Date, Double> storage = RAMStorageModule.create();
        storage.insert(controlGroups);
        Date beginKey = dateFormat.parse("02/02/2016 02:02 AM");
        Date endKey = dateFormat.parse("04/04/2016 04:04 AM");
        storage.delete(beginKey, endKey);
        List<ChartControlGroup<Date, Double>> groups = storage.selectAll();
        assertEquals(groups.size(), 2);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void removeAllControlGroups() throws InsertGroupsException, DeleteGroupsException {
        StorageModule<Date, Double> storage = RAMStorageModule.create();
        storage.insert(controlGroups);
        storage.deleteAll();
        assertEquals(storage.size(), 0);
    }
}
