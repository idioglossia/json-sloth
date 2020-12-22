package lab.idioglossia.jsonsloth;

import lab.idioglossia.sloth.collection.Value;
import lombok.Getter;

@Getter
public class SaveValue implements Value<String> {
    private final String data;

    public SaveValue(String data) {
        this.data = data;
    }
}
