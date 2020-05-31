package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * this class must correspond to MessageStatisticsJson in the app project!
 */
@Entity
public class MessageStatistics extends Model {

    @Id
    @GeneratedValue
    private Long id;

    public Long clientEventId;

    public String userUuid;

    public Integer characterCountAdded;

    public Integer characterCountAltered;

    public Integer characterCountSubmitted;

    public String inputTargetApp;

    public Long timestampTypeStart;

    public Long timestampTypeEnd;

    public String fieldHintText;

    public Integer getCharacterCountAdded() {
        return characterCountAdded;
    }

    public void setCharacterCountAdded(Integer characterCountAdded) {
        this.characterCountAdded = characterCountAdded;
    }

    public Integer getCharacterCountAltered() {
        return characterCountAltered;
    }

    public void setCharacterCountAltered(Integer characterCountAltered) {
        this.characterCountAltered = characterCountAltered;
    }

    public Integer getCharacterCountSubmitted() {
        return characterCountSubmitted;
    }

    public void setCharacterCountSubmitted(Integer characterCountSubmitted) {
        this.characterCountSubmitted = characterCountSubmitted;
    }

    public String getInputTargetApp() {
        return inputTargetApp;
    }

    public void setInputTargetApp(String inputTargetApp) {
        this.inputTargetApp = inputTargetApp;
    }

    public Long getClientEventId() {
        return clientEventId;
    }

    public void setClientEventId(Long clientEventId) {
        this.clientEventId = clientEventId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Long getTimestampTypeStart() {
        return timestampTypeStart;
    }

    public void setTimestampTypeStart(Long timestampTypeStart) {
        this.timestampTypeStart = timestampTypeStart;
    }

    public Long getTimestampTypeEnd() {
        return timestampTypeEnd;
    }

    public void setTimestampTypeEnd(Long timestampTypeEnd) {
        this.timestampTypeEnd = timestampTypeEnd;
    }

    public String getFieldHintText() {
        return fieldHintText;
    }

    public void setFieldHintText(String fieldHintText) {
        this.fieldHintText = fieldHintText;
    }

    public Long getId() {
        return id;
    }
}
