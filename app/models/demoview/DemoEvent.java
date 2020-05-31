package models.demoview;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.AbstractedActionEvent;
import play.libs.Json;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DemoEvent {

    private DemoEvent(){}

    private String type;

    private String content;

    private String app;

    public static DemoEvent fromAbstractedActionEvent(AbstractedActionEvent aae){
        DemoEvent demoEvent = new DemoEvent();
        JsonNode eventJson = Json.parse(aae.getEventJson());
        demoEvent.type = eventJson.get("contentUnitEventType").asText();
        StringBuilder stringBuilder = new StringBuilder();

        if (aae.isCategoryEvent()){
            if (eventJson.has("categoryBefore")){
                stringBuilder.append(eventJson.get("categoryBefore").asText().replaceAll(",",", "));
            }
            if (eventJson.has("categoryBefore") && eventJson.has("categoryAfter")){
                stringBuilder.append(" => ");
            }
            if (eventJson.has("categoryAfter")){
                stringBuilder.append(eventJson.get("categoryAfter").asText().replaceAll(",",", "));
            }
        }
        else if (aae.isRegexMatcherEvent()){
            if (eventJson.has("rawContentBefore")) {
                stringBuilder.append(eventJson.get("rawContentBefore").asText());
            }
            if (eventJson.has("rawContentBefore") && eventJson.has("rawContentAfter")){
                stringBuilder.append(" => ");
            }
            if (eventJson.has("rawContentAfter")) {
                if (eventJson.get("rawContentAfter") instanceof ArrayNode){
                    ArrayNode emojiArray = (ArrayNode) eventJson.get("rawContentAfter");
                    for(int i = 0; i<emojiArray.size(); i++) {
                        stringBuilder.append(emojiArray.get(i));
                        if (i<emojiArray.size()-1){
                            stringBuilder.append(",");
                        }
                    }
                }
                else {
                    stringBuilder.append(eventJson.get("rawContentAfter").asText());
                }
            }
        }

        demoEvent.content = stringBuilder.toString();
        return demoEvent;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        Pattern p = Pattern.compile("([0-9]*,)+[0-9]*");
        Matcher m = p.matcher(content);
        boolean b = m.matches();
        if (b){
            String[] asciiCodeStrings = content.split(",");
            char[] asciiChars = new char[asciiCodeStrings.length];
            for (int i = 0; i<asciiCodeStrings.length; i++){
                asciiChars[i] = (char) Integer.valueOf(asciiCodeStrings[i]).intValue();
            }
            return new String(asciiChars);
        }
        return content;
    }
}
