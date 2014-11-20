package hr.simplesource.backpack;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

public class StoreAndGetSyncTestCase extends AndroidTestCase {

    Backpack backpack;

    private List<TestModel> testModelList10 = new ArrayList<TestModel>();
    private List<TestModel> testModelList100 = new ArrayList<TestModel>();
    private List<TestModel> testModelList1000 = new ArrayList<TestModel>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        backpack = Backpack.initialize(getContext())
                .build();

        for (int i = 0; i < 1000; i++) {
            if (i < 10) {
                testModelList10.add(TestModel.generateRandom());
            }
            if (i < 100) {
                testModelList100.add(TestModel.generateRandom());
            }
            testModelList1000.add(TestModel.generateRandom());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testModelList10.clear();
        testModelList10 = null;

        testModelList100.clear();
        testModelList100 = null;

        testModelList1000.clear();
        testModelList1000 = null;

        backpack.wipeAllSync();
        backpack = null;
    }

    public void test_store10TestModels() throws Exception {
        assertTrue(testModelList10.size() == 10);
        for (TestModel tm : testModelList10) {
            assertNotNull(tm);
        }
        backpack.storeManySync(testModelList10);
    }

    public void test_store100TestModels() throws Exception {
        assertTrue(testModelList100.size() == 100);
        for (TestModel tm : testModelList100) {
            assertNotNull(tm);
        }
        backpack.storeManySync(testModelList100);
    }

    public void test_store1000TestModels() throws Exception {
        assertTrue(testModelList1000.size() == 1000);
        for (TestModel tm : testModelList1000) {
            assertNotNull(tm);
        }
        backpack.storeManySync(testModelList1000);
    }

    public void test_get10TestModels() throws Exception {
        backpack.storeManySync(testModelList10);

        List<TestModel> loadedTestModels = new ArrayList<TestModel>();
        for (TestModel model : testModelList10) {
            TestModel loadedModel = backpack.get(String.valueOf(model.hashCode()), TestModel.class);
            loadedTestModels.add(loadedModel);
        }
        assertTrue(loadedTestModels.size() == 10);
        for (TestModel tm : loadedTestModels) {
            assertNotNull(tm);
        }
    }

    public void test_get100TestModels() throws Exception {
        backpack.storeManySync(testModelList100);

        List<TestModel> loadedTestModels = new ArrayList<TestModel>();
        for (TestModel model : testModelList100) {
            loadedTestModels.add(backpack.get(String.valueOf(model.hashCode()), TestModel.class));
        }
        assertTrue(loadedTestModels.size() == 100);
        for (TestModel tm : loadedTestModels) {
            assertNotNull(tm);
        }
    }

    public void test_get1000TestModels() throws Exception {
        backpack.storeManySync(testModelList1000);

        List<TestModel> loadedTestModels = new ArrayList<TestModel>();
        for (TestModel model : testModelList1000) {
            loadedTestModels.add(backpack.get(String.valueOf(model.hashCode()), TestModel.class));
        }
        assertTrue(loadedTestModels.size() == 1000);
        for (TestModel tm : loadedTestModels) {
            assertNotNull(tm);
        }
    }

//    public void test_getAll() throws Exception {
//        List<TestModel> loadedTestModels = new ArrayList<TestModel>();
//        loadedTestModels.addAll(backpack.getAll(TestModel.class));
//        for (TestModel tm : loadedTestModels) {
//            assertNotNull(tm);
//        }
//        assertEquals(1110, loadedTestModels.size());
//    }

    public void test_getAllExpectEmpty() throws Exception {
        List<TestModel> loadedTestModels = new ArrayList<TestModel>();
        loadedTestModels.addAll(backpack.getAll(TestModel.class));
        for (TestModel tm : loadedTestModels) {
            assertNotNull(tm);
        }
        assertEquals(0, loadedTestModels.size());
    }

    public void test_wipeAll() {
        backpack.storeManySync(testModelList10);
        backpack.storeManySync(testModelList100);
        backpack.storeManySync(testModelList1000);

        List<TestModel> checkSizeList = new ArrayList<TestModel>();
        checkSizeList.addAll(backpack.getAll(TestModel.class));
        assertEquals(1110, checkSizeList.size());

        backpack.wipeAllSync();

        List<TestModel> checkSizeListAfterWipe = new ArrayList<TestModel>();
        checkSizeListAfterWipe.addAll(backpack.getAll(TestModel.class));
        assertEquals(0, checkSizeListAfterWipe.size());
    }

}
