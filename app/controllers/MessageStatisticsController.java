package controllers;

import models.MessageStatistics;
import play.mvc.Result;

public class MessageStatisticsController extends JsonPostController<MessageStatistics> {
    @Override
    protected Class<MessageStatistics> getClassType() {
        return MessageStatistics.class;
    }

    public Result postMessageStatistics() {
        return processRequestArrayData("messageStatistics");
    }

}