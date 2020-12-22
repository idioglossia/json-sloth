package lab.idioglossia.jsonsloth;

import lab.idioglossia.sloth.collection.Collection;
import lab.idioglossia.sloth.storage.Storage;
import lab.idioglossia.sloth.storage.StorageDecorator;

import java.io.Serializable;

public class JsonSlothStorage extends StorageDecorator {

    public JsonSlothStorage(Storage storage) {
        super(storage);
    }

    @Override
    public <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass) {
        return super.getCollectionOfType(name, type, dataClass, ".json");
    }

    @Override
    public <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass, String extension) {
        return super.getCollectionOfType(name, type, dataClass, ".json");
    }
}
