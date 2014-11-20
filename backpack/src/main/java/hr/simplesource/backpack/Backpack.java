package hr.simplesource.backpack;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Backpack {

    public interface StoreSingleListener {
        <T extends Storable> void onStoreSingleCompleted(Backpack backpack, T storable);
    }

    public interface StoreManyListener {
        <E extends Storable> void onStoreManyCompleted(Backpack backpack, List<E> storables);
    }

    public static Backpack INSTANCE;

    public static Builder initialize(Context context) {
        return new Builder(context);
    }

    public static Backpack use() {
        if (INSTANCE == null) {
            throw new NullPointerException("Storage has not been initialized. Call " +
                    "Storage.initialize() method and set Storage.INSTANCE manually.");
        }

        return INSTANCE;
    }

    private final Gson mGson;
    private final Context mContext;
    private final StoreSingleListener mStoreSingleListener;
    private final StoreManyListener mStoreManyListener;
    private final boolean mThrowExceptions;

    private Backpack(Context context, Gson gson, boolean throwExceptions,
                     StoreSingleListener singleListener, StoreManyListener manyListener) {
        mContext = context;
        mStoreSingleListener = singleListener;
        mStoreManyListener = manyListener;
        mGson = gson;
        mThrowExceptions = throwExceptions;
    }

    private void writeToFile(String name, String content) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(mContext.openFileOutput(name, Context.MODE_PRIVATE));
            writer.write(content);
            writer.close();
        } catch (FileNotFoundException e) {
            if (mThrowExceptions) {
                throw new RuntimeException("Exception occurred while trying to write to file with message: " +
                        e.getMessage(), e);
            }
        } catch (IOException e) {
            if (mThrowExceptions) {
                throw new RuntimeException("Exception occurred while trying to write to file with message: " +
                        e.getMessage(), e);
            }
        }
    }

    private String readFromFile(String name) {
        String readValue = "";
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(mContext.openFileInput(name));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String buffer = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((buffer = reader.readLine()) != null) {
                stringBuilder.append(buffer);
            }
            readValue = stringBuilder.toString();
            reader.close();
            inputStreamReader.close();
        } catch (FileNotFoundException e) {
            if (mThrowExceptions) {
                throw new RuntimeException("Exception occurred while trying to read from file with message: " +
                        e.getMessage(), e);
            }
        } catch (IOException e) {
            if (mThrowExceptions) {
                throw new RuntimeException("Exception occurred while trying to read from file with message: " +
                        e.getMessage(), e);
            }
        }
        return readValue;
    }

    private void storeStorable(Storable storable) {
        String content = mGson.toJson(storable);
        String name = StoreUtil.getName(storable.storableDescription(), storable.getClass());
        writeToFile(name, content);
    }

    public <T extends Storable> void store(final T storable) {
        if (storable.storableDescription() == null) {
            throw new RuntimeException("Storable description must not be null. This is a unique" +
                    " identifier of your data.");
        }

        Task storeTask = new Task.Builder(new Work() {
            @Override
            public void work() {
                storeStorable(storable);
            }
        }).after(new Work() {
            @Override
            public void work() {
                if (mStoreSingleListener != null) {
                    mStoreSingleListener.onStoreSingleCompleted(Backpack.this, storable);
                }
            }
        }).build();
        storeTask.execute();
    }

    public <T extends Storable> void storeSync(final T storable) {
        if (storable.storableDescription() == null) {
            throw new RuntimeException("Storable description must not be null. This is a unique" +
                    " identifier of your data.");
        }

        storeStorable(storable);
    }

    public <E extends Storable> void storeMany(final List<E> list) {
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("List cannot be empty or null. In order to store data, list has to hold data.");
        }

        Task storeManyTask = new Task.Builder(new Work() {
            @Override
            public void work() {
                for (Storable st : list) {
                    if (st.storableDescription() == null) {
                        throw new RuntimeException("Storable description must not be null. This is a unique" +
                                " identifier of your data.");
                    }

                    storeStorable(st);
                }
            }
        }).after(new Work() {
            @Override
            public void work() {
                if (mStoreManyListener != null) {
                    mStoreManyListener.onStoreManyCompleted(Backpack.this, list);
                }
            }
        }).build();
        storeManyTask.execute();
    }

    public <E extends Storable> void storeManySync(final List<E> list) {
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("List cannot be empty or null. In order to store data, list has to hold data.");
        }

        for (Storable st : list) {
            if (st.storableDescription() == null) {
                throw new RuntimeException("Storable description must not be null. This is a unique" +
                        " identifier of your data.");
            }

            storeStorable(st);
        }
    }

    public <T extends Storable> T get(String description, Class<T> type) {
        String content = readFromFile(StoreUtil.getName(description, type));
        return mGson.fromJson(content, type);
    }

    public <E extends Storable> List<E> getAll(Class<E> type) {
        List<String> fileList = new ArrayList<String>();
        StoreUtil.getFileListForType(type, mContext.fileList(), fileList);
        List<E> storableList = new ArrayList<E>();
        for (String file : fileList) {
            storableList.add(mGson.fromJson(readFromFile(file), type));
        }

        return storableList;
    }

    public <E extends Storable> List<E> getFiltered(Class<E> type, StorableFilter<E> filter) {
        List<String> fileList = new ArrayList<String>();
        StoreUtil.getFileListForType(type, mContext.fileList(), fileList);
        List<E> storableList = new ArrayList<E>();
        for (String file : fileList) {
            E storable = mGson.fromJson(readFromFile(file), type);
            if (filter.filter(storable)) {
                storableList.add(storable);
            }
        }
        return storableList;
    }

    public <T extends Storable> void wipe(Class<T> type) {
        final List<String> fileList = new ArrayList<String>();
        StoreUtil.getFileListForType(type, mContext.fileList(), fileList);

        Task removeTask = new Task.Builder(new Work() {
            @Override
            public void work() {
                for (String file : fileList) {
                    mContext.deleteFile(file);
                }
            }
        }).build();
        removeTask.execute();
    }

    public <T extends Storable> void wipeSync(Class<T> type) {
        final List<String> fileList = new ArrayList<String>();
        StoreUtil.getFileListForType(type, mContext.fileList(), fileList);
        for (String file : fileList) {
            mContext.deleteFile(file);
        }
    }

    public void wipeAll() {
        List<String> fileList = new ArrayList<String>();

        Task removeTask = new Task.Builder(new Work() {
            @Override
            public void work() {
                for (String file : mContext.fileList()) {
                    mContext.deleteFile(file);
                }
            }
        }).build();
        removeTask.execute();
    }

    public void wipeAllSync() {
        for (String file : mContext.fileList()) {
            mContext.deleteFile(file);
        }
    }

    public static class Builder {
        private final Context mContext;
        private StoreSingleListener mStoreSingleListener;
        private StoreManyListener mStoreManyListener;
        private Gson mGson;
        private boolean mThrowExceptions;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            } else {
                this.mContext = context.getApplicationContext();
            }
        }



        public Builder withStoreSingleListener(StoreSingleListener storeSingleListener) {
            if (storeSingleListener == null) {
                throw new IllegalArgumentException("StoreSingleListener must not be null");
            } else if (this.mStoreSingleListener != null) {
                throw new IllegalArgumentException("StoreSingleListener is already set");
            } else {
                this.mStoreSingleListener = storeSingleListener;
            }
            return this;
        }

        public Builder withStoreManyListener(StoreManyListener storeManyListener) {
            if (storeManyListener == null) {
                throw new IllegalArgumentException("StoreManyListener must not be null");
            } else if (this.mStoreManyListener != null) {
                throw new IllegalArgumentException("StoreManyListener is already set");
            } else {
                this.mStoreManyListener = storeManyListener;
            }
            return this;
        }

        public Builder useGson(Gson gson) {
            if (gson == null) {
                throw new IllegalArgumentException("Gson must not be null");
            } else if (this.mGson != null) {
                throw new IllegalArgumentException("Gson is already set");
            } else {
                this.mGson = gson;
            }
            return this;
        }

        public Builder throwExceptions(boolean shouldThrow) {
            this.mThrowExceptions = shouldThrow;
            return this;
        }

        public Backpack build() {
            if (mGson == null) {
                mGson = DefaultInitializations.sDefaultGson;
            }

            return new Backpack(this.mContext, mGson, mThrowExceptions, mStoreSingleListener, mStoreManyListener);
        }
    }

    private static class DefaultInitializations {
        public static Gson sDefaultGson = new Gson();
    }

}
