package hr.simplesource.backpack;

import com.google.gson.annotations.SerializedName;

import java.util.Random;
import java.util.UUID;

public class TestModel implements Storable {

    private static final Random sRand = new Random();

    @SerializedName("id")
    private int id;
    @SerializedName("string")
    private String randomUniversalId;
    @SerializedName("double")
    private double randomDouble;
    @SerializedName("boolean")
    private boolean randomBoolean;

    public TestModel(int id, String randomUniversalId, double randomDouble, boolean randomBoolean) {
        this.id = id;
        this.randomUniversalId = randomUniversalId;
        this.randomDouble = randomDouble;
        this.randomBoolean = randomBoolean;
    }

    public int getId() {
        return id;
    }

    public String getRandomUniversalId() {
        return randomUniversalId;
    }

    public double getRandomDouble() {
        return randomDouble;
    }

    public boolean isRandomBoolean() {
        return randomBoolean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestModel testModel = (TestModel) o;

        if (id != testModel.id) return false;
        if (randomBoolean != testModel.randomBoolean) return false;
        if (Double.compare(testModel.randomDouble, randomDouble) != 0) return false;
        if (randomUniversalId != null ? !randomUniversalId.equals(testModel.randomUniversalId) : testModel.randomUniversalId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (randomUniversalId != null ? randomUniversalId.hashCode() : 0);
        temp = Double.doubleToLongBits(randomDouble);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (randomBoolean ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestModel{" +
                "id=" + id +
                ", randomUniversalId='" + randomUniversalId + '\'' +
                ", randomDouble=" + randomDouble +
                ", randomBoolean=" + randomBoolean +
                '}';
    }

    @Override
    public String storableDescription() {
        return String.valueOf(hashCode());
    }

    public static TestModel generateRandom() {
        return new TestModel(
                sRand.nextInt(),
                UUID.randomUUID().toString(),
                sRand.nextDouble(),
                sRand.nextBoolean()
        );
    }

    public static TestModel generateRandom(boolean setBoolean) {
        return new TestModel(
                sRand.nextInt(),
                UUID.randomUUID().toString(),
                sRand.nextDouble(),
                setBoolean
        );
    }
}
