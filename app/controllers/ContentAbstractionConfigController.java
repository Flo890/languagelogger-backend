package controllers;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import database.PatternMatcherDatabase;
import database.WordlistDatabase;
import models.config.PatternMatcherConfig;
import models.config.RIMEContentAbstractionConfig;
import models.wordlists.*;
import play.api.http.HttpEntity;
import play.api.mvc.ResponseHeader;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ContentAbstractionConfigController extends Controller {

    @Inject
    private FormFactory formFactory;

    public Result getCategoryAndWordListsConfigs() {

        RIMEContentAbstractionConfig rimeContentAbstractionConfig = new RIMEContentAbstractionConfig();

        List<LogicalCategoryList> activeCategoryLists = new ArrayList<>();
        for(LogicalCategoryList logicalCategoryList : WordlistDatabase.getLogicalCategoryLists()){
            if (logicalCategoryList.isActive()){
                activeCategoryLists.add(logicalCategoryList);
            }
        }
        rimeContentAbstractionConfig.setLogicalCategoryLists(activeCategoryLists);

        List<LogicalWordList> activeLogicalWordLists = new ArrayList<>();
        for (LogicalWordList logicalWordList : WordlistDatabase.getLogicalWordLists()) {
            if (logicalWordList.isActive()) {
                activeLogicalWordLists.add(logicalWordList);
            }
        }
        rimeContentAbstractionConfig.setLogicalWordLists(activeLogicalWordLists);

        List<PatternMatcherConfig> patternMatcherConfigs = new ArrayList<>();
        for (PatternMatcherConfig patternMatcherConfig : PatternMatcherDatabase.getPatternMatchers()) {
            if (patternMatcherConfig.isActive()) {
                patternMatcherConfigs.add(patternMatcherConfig);
            }
        }
        rimeContentAbstractionConfig.setPatternMatcherConfigs(patternMatcherConfigs);

        JsonNode configNode = Json.toJson(rimeContentAbstractionConfig);
        return ok(configNode);
    }

    public Result getLogicalCategoryListFile(String logicallistId){
        LogicalCategoryList logicalCategoryList = WordlistDatabase.getLogicalCategoryList(logicallistId);
        StringBuilder stringBuilder = new StringBuilder();
        for(CategoryBaseList categoryBaseList : logicalCategoryList.getIncludesCategoryBaseLists()){
            for(Word2CategoryMapping word2CategoryMapping : categoryBaseList.getWord2CategoryMappings()) {
                stringBuilder.append(word2CategoryMapping.getWord()).append("\t").append(word2CategoryMapping.getCategory()).append("\n");
            }
        }
        return ok(stringBuilder.toString());
    }

    public Result getLogicalWordListFile(String logicallistId){
        LogicalWordList logicalWordList = WordlistDatabase.getLogicalWordList(logicallistId);
        StringBuilder stringBuilder = new StringBuilder();
        for(WordBaseList wordBaseList : logicalWordList.getIncludesWordBaseLists()){
            for(Word word : wordBaseList.getWords()) {
                stringBuilder.append(word.getWord()).append("\n");
            }
        }
        return ok(stringBuilder.toString());
    }
}
