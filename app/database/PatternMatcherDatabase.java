package database;

import com.avaje.ebean.Ebean;
import models.config.PatternMatcherConfig;
import play.Logger;

import java.util.List;

public class PatternMatcherDatabase {

    public static List<PatternMatcherConfig> getPatternMatchers(){
        List<PatternMatcherConfig> patternMatcherConfigs = Ebean.find(PatternMatcherConfig.class).findList();
        if (patternMatcherConfigs == null || patternMatcherConfigs.isEmpty()) {
            //categoryBaseLists = createFromConfig();
        }
        return patternMatcherConfigs;
    }

    public static PatternMatcherConfig getPatternMatcher(String patternMatcherId) {
        if(patternMatcherId == null){return null;}
        return Ebean.find(PatternMatcherConfig.class)
                .where().eq("regex_matcher_id", patternMatcherId)
                .findUnique();
    }

    public static boolean isPatternMatcherDBEmpty(){
        return Ebean.find(PatternMatcherConfig.class).findRowCount() == 0;
    }

    public static boolean deleteCategoryBaselist(String id) {
        PatternMatcherConfig patternMatcherConfig = getPatternMatcher(id);
        if(patternMatcherConfig != null){
            try{
                patternMatcherConfig.delete();
                return true;
            }catch (Exception e){
                Logger.warn("could not delete pattern matcher", e);
            }
        }
        return false;
    }
}
