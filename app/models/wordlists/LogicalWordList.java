package models.wordlists;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import database.WordlistDatabase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class LogicalWordList extends Model {

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
    private List<WordBaseList> includesWordBaseLists;

    public static LogicalWordList createLogicalWordList(String logicallistName, String description, boolean preappyLemmaExtraction) {
        LogicalWordList logicalWordList = new LogicalWordList();
        logicalWordList.logicallistName = logicallistName;
        logicalWordList.description = description;
        logicalWordList.preappyLemmaExtraction = preappyLemmaExtraction;
        logicalWordList.active = true; // TODO make configurable!
        return logicalWordList;
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
        for (WordBaseList baselist : getIncludesWordBaseLists()){
            wordCount += baselist.getWords().size();
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

    public List<WordBaseList> getIncludesWordBaseLists() {
        return includesWordBaseLists;
    }

    public boolean isActive() {
        return active;
    }

    public void update(LogicalWordList logicallist) {
        logicallistName = logicallist.logicallistName;
        description = logicallist.description;
        preappyLemmaExtraction = logicallist.preappyLemmaExtraction;
        update();
    }

    @JsonIgnore
    public List<String> getConfigWarnings(){
        List<String> warnings = new ArrayList<>();

        if (includesWordBaseLists.size() == 0) {
            warnings.add("you should select at least one base list");
        }

        return warnings;
    }

}
