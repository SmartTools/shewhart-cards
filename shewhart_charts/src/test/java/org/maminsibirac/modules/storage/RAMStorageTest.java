package org.maminsibirac.modules.storage;

import org.junit.Before;
import org.junit.Test;
import org.maminsibirac.shewhart.groups.ControlGroup;
import org.maminsibirac.shewhart.modules.storage.RAMStorage;
import org.maminsibirac.shewhart.modules.storage.StorageChartModule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RAMStorageTest {
    private StorageChartModule<Date> storageChartModule;
    private DateFormat dateFormat;
    private List<TestControlGroup<Date>> controlGroups;

    private class TestControlGroup<TKey extends Comparable<TKey>> implements ControlGroup<TKey> {
        private TKey key;

        public TestControlGroup(TKey key) {
            this.key = key;
        }

        @Override
        public TKey getKey() {
            return key;
        }
    }

    @Before
    public void setUp() throws ParseException {
        storageChartModule = RAMStorage.create();
        dateFormat = new SimpleDateFormat();

        controlGroups = new LinkedList<>();
        TestControlGroup<Date> firstGroup = mock(TestControlGroup.class);
        TestControlGroup<Date> secondGroup = mock(TestControlGroup.class);
        TestControlGroup<Date> thirdGroup = mock(TestControlGroup.class);
        TestControlGroup<Date> fourthGroup = mock(TestControlGroup.class);
        TestControlGroup<Date> fiveGroup = mock(TestControlGroup.class);

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

        storageChartModule.save(controlGroups);
    }

    @Test
    public void getControlGroupsByBeginKey() throws ParseException {
        Date beginKey = dateFormat.parse("03/03/2016 03:03 AM");
        List<TestControlGroup<Date>> groups = (List<TestControlGroup<Date>>) storageChartModule.get(beginKey);
        assertEquals(groups.size(), 3);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void getControlGroupsByBeginAndEndKeys() throws ParseException {
        Date beginKey = dateFormat.parse("02/02/2016 02:02 AM");
        Date endKey = dateFormat.parse("04/04/2016 04:04 AM");
        List<TestControlGroup<Date>> groups = (List<TestControlGroup<Date>>) storageChartModule.get(beginKey, endKey);
        assertEquals(groups.size(), 3);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
    }

    @Test
    public void getAllControlGroups() throws ParseException {
        List<TestControlGroup<Date>> groups = (List<TestControlGroup<Date>>) storageChartModule.getAll();
        assertEquals(groups.size(), 5);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("03/03/2016 03:03 AM"));
        assertEquals(groups.get(3).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(4).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));

    }

    @Test
    public void removeControlGroupsByKey() throws ParseException {
        StorageChartModule<Date> storage = RAMStorage.create();
        storage.save(controlGroups);

        Date key = dateFormat.parse("03/03/2016 03:03 AM");
        storage.remove(key);
        List<TestControlGroup<Date>> groups = (List<TestControlGroup<Date>>) storage.getAll();
        assertEquals(groups.size(), 4);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("02/02/2016 02:02 AM"));
        assertEquals(groups.get(2).getKey(), dateFormat.parse("04/04/2016 04:04 AM"));
        assertEquals(groups.get(3).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void removeControlGroupsByBeginAndEndKeys() throws ParseException {
        StorageChartModule<Date> storage = RAMStorage.create();
        storage.save(controlGroups);
        Date beginKey = dateFormat.parse("02/02/2016 02:02 AM");
        Date endKey = dateFormat.parse("04/04/2016 04:04 AM");
        storage.remove(beginKey, endKey);
        List<TestControlGroup<Date>> groups = (List<TestControlGroup<Date>>) storage.getAll();
        assertEquals(groups.size(), 2);
        assertEquals(groups.get(0).getKey(), dateFormat.parse("01/01/2016 01:01 AM"));
        assertEquals(groups.get(1).getKey(), dateFormat.parse("05/05/2016 05:05 AM"));
    }

    @Test
    public void removeAllControlGroups() {
        StorageChartModule<Date> storage = RAMStorage.create();
        storage.save(controlGroups);
        storage.removeAll();
        assertEquals(storage.size(), 0);
    }

}
