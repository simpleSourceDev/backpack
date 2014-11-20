package hr.simplesource.backpack;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

public class TunedTestModelTestCase extends AndroidTestCase {

    Backpack backpack;

    private List<TestModel> testModelList = new ArrayList<TestModel>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        backpack = Backpack.initialize(getContext())
                .build();

        testModelList.add(TestModel.generateRandom(false));
        testModelList.add(TestModel.generateRandom(false));
        testModelList.add(TestModel.generateRandom(false));
        testModelList.add(TestModel.generateRandom(false));
        testModelList.add(TestModel.generateRandom(false));
        testModelList.add(TestModel.generateRandom(false));
        testModelList.add(TestModel.generateRandom(true));
        testModelList.add(TestModel.generateRandom(true));
        testModelList.add(TestModel.generateRandom(true));
        testModelList.add(TestModel.generateRandom(true));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testModelList.clear();
        testModelList = null;
        
        backpack.wipeAllSync();
        backpack = null;
    }

    public void test_getWithFilter() throws Exception {
        backpack.storeManySync(testModelList);

        List<TestModel> loadedTestModels = new ArrayList<TestModel>();
        loadedTestModels.addAll(backpack.getFiltered(TestModel.class, new StorableFilter<TestModel>() {
            @Override
            public boolean filter(TestModel storable) {
                return storable.isRandomBoolean();
            }
        }));
        assertEquals(4, loadedTestModels.size());
    }

}
