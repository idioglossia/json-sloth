package lab.idioglossia.jsonsloth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lab.idioglossia.sloth.collection.Collection;
import lab.idioglossia.sloth.collection.Value;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JsonSlothManager {
    private final JsonSlothStorage jsonSlothStorage;
    private final ObjectMapper objectMapper;

    public JsonSlothManager(JsonSlothStorage jsonSlothStorage, ObjectMapper objectMapper) {
        this.jsonSlothStorage = jsonSlothStorage;
        this.objectMapper = objectMapper;
    }

    public <K> List<K> getKeys(Class<?> aClass){
        JsonSlothEntity jsonSlothEntity = getValidJsonSlothEntity(aClass);
        Collection<K, String> collectionOfType = jsonSlothStorage.getCollectionOfType(jsonSlothEntity.collectionName(), jsonSlothEntity.type(), String.class);
        return new ArrayList<K>(collectionOfType.getKeys());
    }

    @SneakyThrows
    public void save(Object object) {
        JsonSlothEntity jsonSlothEntity = getValidJsonSlothEntity(object.getClass());
        Field jsonSlothIdField = getValidJsonSlothIdField(object.getClass().getDeclaredFields());
        if(jsonSlothEntity.type().equals(Collection.Type.LIST) && jsonSlothIdField.get(object) != null)
            update(object, jsonSlothEntity, jsonSlothIdField);
        else
            submit(object, jsonSlothEntity, jsonSlothIdField);
    }

    public int size(Class<?> aClass){
        JsonSlothEntity jsonSlothEntity = getValidJsonSlothEntity(aClass);
        return jsonSlothStorage.getCollectionOfType(jsonSlothEntity.collectionName(), jsonSlothEntity.type(), String.class).size();
    }

    @SneakyThrows
    public void update(Object object){
        JsonSlothEntity jsonSlothEntity = getValidJsonSlothEntity(object.getClass());
        Field jsonSlothIdField = getValidJsonSlothIdField(object.getClass().getDeclaredFields());
        if(jsonSlothEntity.type().equals(Collection.Type.LIST) && jsonSlothIdField.get(object) == null)
            submit(object, jsonSlothEntity, jsonSlothIdField);
        else
            update(object, jsonSlothEntity, jsonSlothIdField);
    }

    @SneakyThrows
    public void delete(Object object){
        JsonSlothEntity jsonSlothEntity = getValidJsonSlothEntity(object.getClass());
        Field jsonSlothIdField = getValidJsonSlothIdField(object.getClass().getDeclaredFields());

        Collection<Object, String> collection = jsonSlothStorage.getCollectionOfType(jsonSlothEntity.collectionName(), jsonSlothEntity.type(), String.class);
        assert jsonSlothIdField.get(object) != null;
        collection.remove(jsonSlothIdField.get(object));
    }

    @SneakyThrows
    public <E, K> E get(K key, Class<E> dataType){
        JsonSlothEntity jsonSlothEntity = getValidJsonSlothEntity(dataType);
        return get(jsonSlothEntity.collectionName(), key, dataType);
    }

    @SneakyThrows
    public <E, K> E get(String collectionName, K key, Class<E> dataType){
        JsonSlothEntity jsonSlothEntity = getValidJsonSlothEntity(dataType);
        Field jsonSlothIdField = getValidJsonSlothIdField(dataType.getDeclaredFields());

        Collection<K, String> collection = jsonSlothStorage.getCollectionOfType(collectionName, jsonSlothEntity.type(), String.class);
        Value<String> value = collection.get(key);
        if(value.exists()){
            E result = objectMapper.readValue(value.getData(), dataType);
            setId(value, jsonSlothIdField, result);
            return result;
        }
        return null;
    }

    @SneakyThrows
    private void update(Object object, JsonSlothEntity jsonSlothEntity, Field jsonSlothIdField) {
        final String json = objectMapper.writeValueAsString(object);
        Collection<String, String> collection = jsonSlothStorage.getCollectionOfType(jsonSlothEntity.collectionName(), jsonSlothEntity.type(), String.class);
        collection.update(getKey(jsonSlothIdField, object), new SaveValue(json));
    }

    public JsonSlothStorage jsonSlothStorage(){
        return this.jsonSlothStorage;
    }

    @SneakyThrows
    private void submit(Object object, JsonSlothEntity jsonSlothEntity, Field jsonSlothIdField) {
        final String json = objectMapper.writeValueAsString(object);
        Collection<String, String> collection = jsonSlothStorage.getCollectionOfType(jsonSlothEntity.collectionName(), jsonSlothEntity.type(), String.class);
        Value<String> saveValue;
        if(jsonSlothEntity.type().equals(Collection.Type.MAP)){
            String key = getKey(jsonSlothIdField, object);
            saveValue = collection.save(key, new SaveValue(json));
        }else {
            saveValue = collection.save(new SaveValue(json));
        }
        setId(saveValue, jsonSlothIdField, object);
    }

    private Field getValidJsonSlothIdField(Field[] fields) {
        Field jsonSlothIdField = getJsonSlothIdField(fields);
        assert jsonSlothIdField != null;
        assert jsonSlothIdField.getType() == Integer.class || jsonSlothIdField.getType() == String.class;
        jsonSlothIdField.setAccessible(true);
        return jsonSlothIdField;
    }

    private <E> JsonSlothEntity getValidJsonSlothEntity(Class<E> dataType) {
        JsonSlothEntity jsonSlothEntity = dataType.getAnnotation(JsonSlothEntity.class);
        assert jsonSlothEntity != null;
        assert dataType.getFields().length > 1;
        return jsonSlothEntity;
    }

    private void setId(Value<String> saveValue, Field jsonSlothIdField, Object object) throws IllegalAccessException {
        if(jsonSlothIdField.getType() == String.class)
            jsonSlothIdField.set(object, saveValue.id());
        else
            jsonSlothIdField.set(object, Integer.parseInt(saveValue.id()));
    }

    private String getKey(Field jsonSlothIdField, Object object) throws IllegalAccessException {
        Object o = jsonSlothIdField.get(object);
        if(jsonSlothIdField.getType() == String.class)
            return (String) o;
        else
            return String.valueOf(o);
    }


    private Field getJsonSlothIdField(Field[] fields){
        for (Field field : fields) {
            if(field.getAnnotation(JsonSlothId.class) != null)
                return field;
        }
        return null;
    }

}
