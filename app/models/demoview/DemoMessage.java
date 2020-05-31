package models.demoview;

import models.AbstractedActionEvent;
import models.MessageStatistics;
import utils.DateConverter;

import java.util.List;

public class DemoMessage {

    private MessageStatistics messageStatistics;

    private List<AbstractedActionEvent> abstractedActionEvents;

    private List<DemoEvent> demoAbstractedActionEvents;

    private List<DemoEvent> demoRegexMatcherEvents;

    public MessageStatistics getMessageStatistics() {
        return messageStatistics;
    }

    public void setMessageStatistics(MessageStatistics messageStatistics) {
        this.messageStatistics = messageStatistics;
    }

    public List<AbstractedActionEvent> getAbstractedActionEvents() {
        return abstractedActionEvents;
    }

    public void setAbstractedActionEvents(List<AbstractedActionEvent> abstractedActionEvents) {
        this.abstractedActionEvents = abstractedActionEvents;
    }

    public List<DemoEvent> getDemoAbstractedActionEvents() {
        return demoAbstractedActionEvents;
    }

    public void setDemoAbstractedActionEvents(List<DemoEvent> demoEvents) {
        this.demoAbstractedActionEvents = demoEvents;
    }

    public void setDemoRegexMatcherEvents(List<DemoEvent> demoEvents) {
        this.demoRegexMatcherEvents = demoEvents;
    }

    public List<DemoEvent> getDemoRegexMatcherEvents() {
        return demoRegexMatcherEvents;
    }

    public String getTimeAsString(){
        if (messageStatistics.getTimestampTypeStart() != null) {
            return DateConverter.getDateStamp(messageStatistics.getTimestampTypeStart()) + " " + DateConverter.getTimeStamp(messageStatistics.getTimestampTypeStart());
        }
        else {
            return "";
        }
    }
}
