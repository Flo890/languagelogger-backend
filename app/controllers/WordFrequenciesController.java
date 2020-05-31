package controllers;

import models.WordFrequency;
import play.mvc.Result;

public class WordFrequenciesController extends JsonPostController<WordFrequency> {
    @Override
    protected Class<WordFrequency> getClassType() {
        return WordFrequency.class;
    }

    public Result postWordFrequencies(){
        return processRequestArrayData("wordfrequencies");
    }
}
