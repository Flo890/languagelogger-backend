package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * this class must correspond to AbtractedAction in the app project!
 */
@Entity
public class AbstractedActionEvent extends Model {

    @Id
    @GeneratedValue
    private Long id;

    public String userUuid;

    private Long clientEventId;

    private Date date;

    @Column(columnDefinition = "TEXT")
    private String eventJson;

    // No foreign real key because could cause sync trouble when a list gets deleted in the backend
    private Long logicalCategoryListId;

    private Long regexMatcherId;

    private Long messageStatisticsId;


    public Long getClientEventId() {
        return clientEventId;
    }

    public void setClientEventId(Long clientEventId) {
        this.clientEventId = clientEventId;
    }

    public String getEventJson() {
        return eventJson;
    }

    public void setEventJson(String eventJson) {
        this.eventJson = eventJson;
    }

    public Long getLogicalCategoryListId() {
        return logicalCategoryListId;
    }

    public void setLogicalCategoryListId(Long logicalCategoryListId) {
        this.logicalCategoryListId = logicalCategoryListId;
    }

    public Long getMessageStatisticsId() {
        return messageStatisticsId;
    }

    public void setMessageStatisticsId(Long messageStatisticsId) {
        this.messageStatisticsId = messageStatisticsId;
    }

    public Long getRegexMatcherId() {
        return regexMatcherId;
    }

    public void setRegexMatcherId(Long regexMatcherId) {
        this.regexMatcherId = regexMatcherId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRegexMatcherEvent(){
        return getRegexMatcherId() != null;
    }

    public boolean isCategoryEvent(){
        return getLogicalCategoryListId() != null;
    }
}
