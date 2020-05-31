package controllers;

import models.AbstractedActionEvent;
import play.mvc.Result;

public class AbstractedActionEventController extends JsonPostController<AbstractedActionEvent> {
    @Override
    protected Class<AbstractedActionEvent> getClassType() {
        return AbstractedActionEvent.class;
    }

    public Result postEvents() {
        return processRequestArrayData("events");
    }

}
