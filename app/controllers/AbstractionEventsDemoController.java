package controllers;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;
import database.EventDatabase;
import database.UserDatabase;
import models.*;
import models.demoview.DemoEvent;
import models.demoview.DemoMessage;
import play.Logger;
import play.api.db.Database;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.content_abstraction_demo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class AbstractionEventsDemoController extends Controller {

    private static final int MAX_EVENTS_COUNT = 1000;

    private @Inject
    Database db;

    @Security.Authenticated(CmsSecuredParticipant.class)
    public Result index(String userId) {
        if(!session().get(CmsSecuredParticipant.sessionUserKey).equals(userId)){
            return unauthorized();
        }
        Connection connection = db.getConnection();

        List<User> users = new ArrayList<>();
        User user = UserDatabase.getUserById(userId);
        users.add(user);

        List<DashboardEvent> events = new ArrayList<>();
        Thread eventFetcher = new Thread(() -> EventDatabase.fillWithEvents(connection, events, MAX_EVENTS_COUNT));

        eventFetcher.start();

        try {
            eventFetcher.join();
        } catch (InterruptedException e) {
            Logger.warn("error join thread", e);
        }

        int totalEventsCount = EventDatabase.getTotalAbstractedActionEventsCountForUser(user);

        try {
            connection.close();
        } catch (SQLException e) {
            Logger.error("error closing connection ", e);
        }

        List<MessageStatistics> messageStatisticsList = EventDatabase.getLatestMessageStatisticsOfUser(user,10);
        List<DemoMessage> demoMessages = new ArrayList<>();
        for (MessageStatistics messageStatistics : messageStatisticsList){
            DemoMessage demoMessage = new DemoMessage();
            demoMessage.setMessageStatistics(messageStatistics);
            List<AbstractedActionEvent> abstractedActionEvents = EventDatabase.getAbstractedActionEventsForMessageStatistic(messageStatistics);
            demoMessage.setAbstractedActionEvents(abstractedActionEvents);
            List<DemoEvent> demoAbstractedActionEvents = new ArrayList<>();
            List<DemoEvent> demoRegexMatcherEvents = new ArrayList<>();
            for (AbstractedActionEvent abstractedActionEvent : abstractedActionEvents){
                if (abstractedActionEvent.getRegexMatcherId() == null && abstractedActionEvent.getLogicalCategoryListId() != null) {
                    demoAbstractedActionEvents.add(DemoEvent.fromAbstractedActionEvent(abstractedActionEvent));
                }
                else if (abstractedActionEvent.getRegexMatcherId() != null) {
                    demoRegexMatcherEvents.add(DemoEvent.fromAbstractedActionEvent(abstractedActionEvent));
                }
            }
            demoMessage.setDemoAbstractedActionEvents(demoAbstractedActionEvents);
            demoMessage.setDemoRegexMatcherEvents(demoRegexMatcherEvents);
            demoMessages.add(demoMessage);
        }

        List<WordFrequency> wordFrequencies = Ebean.find(WordFrequency.class).where().eq("user_uuid",user.getUserId()).order().desc("count").findList();

        return ok(content_abstraction_demo.render(demoMessages,totalEventsCount,users.size(),wordFrequencies).body().trim()).as("text/html; charset=utf-8");
    }
}
