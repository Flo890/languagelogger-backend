package models.wordlists;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import database.WordlistDatabase;

import javax.persistence.*;
import java.util.*;

/**
 * this class to be similar to the app module project's class package de.lmu.ifi.researchime.contentabstraction.model.config.LogicalCategoryList (JSON mappable)
 */
@Entity
public class LogicalCategoryList extends Model {

    @Id
    @GeneratedValue
    private Long logicallistId;

    private String logicallistName;

    @JsonIgnore
    private String description;

    private boolean preappyLemmaExtraction;

    @JsonIgnore
    private boolean active;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private List<CategoryBaseList> includesCategoryBaseLists;

    public static LogicalCategoryList createLogicalCategoryList(String logicallistName, String description, boolean preappyLemmaExtraction) {
        LogicalCategoryList logicalCategoryList = new LogicalCategoryList();
        logicalCategoryList.logicallistName = logicallistName;
        logicalCategoryList.description = description;
        logicalCategoryList.preappyLemmaExtraction = preappyLemmaExtraction;
        logicalCategoryList.active = true; // TODO make configurable!
        return logicalCategoryList;
    }

    public String getLogicallistId() {
        return String.valueOf(logicallistId);
    }

    public String getLogicallistName() {
        return logicallistName;
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public String getWordCountString(){
        Integer wordCount = 0;
        for (CategoryBaseList baselist : getIncludesCategoryBaseLists()){
            wordCount += baselist.getWord2CategoryMappings().size();
        }
        if (wordCount<100){
            return String.valueOf(wordCount);
        }
        else if(wordCount<10000){
            return Double.valueOf(Math.floor(wordCount/1000)).intValue()+","+Double.valueOf(Math.floor((wordCount%1000)/100)).intValue()+"k";
        }
        else if (wordCount < 1000000) {
            return Double.valueOf(Math.floor(wordCount/1000)).intValue()+"k";
        }
        else {
            return (Double.valueOf(Math.floor(wordCount/100000)).intValue()/10.0)+"M";
        }
    }

    public boolean isPreappyLemmaExtraction() {
        return preappyLemmaExtraction;
    }

    public List<CategoryBaseList> getIncludesCategoryBaseLists() {
        return includesCategoryBaseLists;
    }

    public boolean isActive() {
        return active;
    }

    public void update(LogicalCategoryList logicallist) {
        logicallistName = logicallist.logicallistName;
        description = logicallist.description;
        preappyLemmaExtraction = logicallist.preappyLemmaExtraction;
        update();
    }

    @JsonIgnore
    public List<String> getConfigWarnings(){
        List<String> warnings = new ArrayList<>();

        if (includesCategoryBaseLists.size() == 0) {
            warnings.add("you should select at least one base list");
        }

        Set<String> allCategories = new HashSet<>();
        for (CategoryBaseList includedCategoryBaseList : includesCategoryBaseLists){
            for (String category : WordlistDatabase.getCategoriesWordCountForBaselist(includedCategoryBaseList.getBaselistId()).keySet()){
                if (allCategories.contains(category)){
                    warnings.add("the category "+category+" exists in multiple base lists");
                }
                allCategories.add(category);
            }
        }

        return warnings;
    }

}
