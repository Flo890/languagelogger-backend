package models.config;

import models.wordlists.LogicalCategoryList;
import models.wordlists.LogicalWordList;

import java.util.List;

/**
 * this class has to be similar to the app module project's de.lmu.ifi.researchime.contentabstraction.model.config.RIMEContentAbstractionConfig
 */
public class RIMEContentAbstractionConfig {

    private List<LogicalCategoryList> logicalCategoryLists;

    private List<LogicalWordList> logicalWordLists;

    private List<PatternMatcherConfig> patternMatcherConfigs;

    public List<LogicalCategoryList> getLogicalCategoryLists() {
        return logicalCategoryLists;
    }

    public void setLogicalCategoryLists(List<LogicalCategoryList> logicalCategoryLists) {
        this.logicalCategoryLists = logicalCategoryLists;
    }

    public List<LogicalWordList> getLogicalWordLists() {
        return logicalWordLists;
    }

    public void setLogicalWordLists(List<LogicalWordList> logicalWordLists) {
        this.logicalWordLists = logicalWordLists;
    }

    public List<PatternMatcherConfig> getPatternMatcherConfigs() {
        return patternMatcherConfigs;
    }

    public void setPatternMatcherConfigs(List<PatternMatcherConfig> patternMatcherConfigs) {
        this.patternMatcherConfigs = patternMatcherConfigs;
    }
}
