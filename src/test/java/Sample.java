import com.fasterxml.jackson.databind.ObjectMapper;
import lab.idioglossia.jsonsloth.JsonSlothEntity;
import lab.idioglossia.jsonsloth.JsonSlothId;
import lab.idioglossia.jsonsloth.JsonSlothManager;
import lab.idioglossia.jsonsloth.JsonSlothStorage;
import lab.idioglossia.sloth.storage.SlothStorage;
import lombok.*;

import java.io.IOException;

public class Sample {

    public static void main(String[] args) throws IOException {
        JsonSlothStorage jsonSlothStorage = new JsonSlothStorage(new SlothStorage(".db/", 3, 10));
        JsonSlothManager jsonSlothManager = new JsonSlothManager(jsonSlothStorage, new ObjectMapper());

        PostEntity post = PostEntity.builder()
                .author("Sepehr-gh")
                .content("Post Content")
                .build();
        jsonSlothManager.save(post);
        System.out.println(post.getId());

        PostEntity postEntity = jsonSlothManager.get(post.getId(), PostEntity.class);
        System.out.println(postEntity);
    }

    @JsonSlothEntity(collectionName = "posts")
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class PostEntity {
        @JsonSlothId
        private Integer id;

        private String author;
        private String content;
    }
}
