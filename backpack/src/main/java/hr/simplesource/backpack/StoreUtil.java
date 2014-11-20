package hr.simplesource.backpack;

import java.util.List;

public class StoreUtil {

    public static <T extends Storable> String getName(String description, Class<T> clazz) {
        return clazz.getName() + "#" + description;
    }

    public static <T> void getFileListForType(Class<T> clazz, String[] filesArray, List<String> fileList) {
        for (String fileName : filesArray) {
            if (fileName.startsWith(clazz.getName())) {
                fileList.add(fileName);
            }
        }
    }

}
