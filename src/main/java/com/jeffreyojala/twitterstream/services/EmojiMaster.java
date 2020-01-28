package com.jeffreyojala.twitterstream.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeffreyojala.twitterstream.models.Emoji;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class to read in emoji.json file and store map of emojis by code
 */
@Component
public class EmojiMaster {

    private Map<String,Emoji> emojisByCodes = new HashMap<>();


    public Optional<Emoji> getEmojiForCodePoint(int codePoint) {
        // create unified code
        String hexCode = Integer.toHexString(codePoint);
        if (emojisByCodes.containsKey(hexCode)) {
            return Optional.of(emojisByCodes.get(hexCode));
        }

        return Optional.empty();
    }

    @PostConstruct
    private void readInEmojiFile() {

        ObjectMapper objectMapper = new ObjectMapper();
        List emojiList = null;
        try {
            String fileName = "static/emoji.json";
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            emojiList = objectMapper.readValue(new FileInputStream(file),List.class);
        } catch (IOException e) {
            // TODO: Critical error, consider emitting event to hault application launch
            e.printStackTrace();
        }
        emojiList.forEach((v) ->  {
            if (v instanceof Map) {
                LinkedHashMap emojiDef = (LinkedHashMap) v;

                if (emojiDef.containsKey("unified") && emojiDef.containsKey("name")) {
                    String code = ((String)emojiDef.get("unified")).toLowerCase();
                    String name = (String)emojiDef.get("name");
                    Emoji emoji = new Emoji();
                    emoji.setUnifiedCode(code);
                    emoji.setName(name);

                    emojisByCodes.put(code, emoji);

                    //System.out.println("Name - " + name + ", " + code);
                }
            }
        });
    }
}
