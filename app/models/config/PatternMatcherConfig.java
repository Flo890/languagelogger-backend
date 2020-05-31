package models.config;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PatternMatcherConfig extends Model {

    @Id
    @GeneratedValue
    private Long regexMatcherId;

    private String regexMatcherName;

    private boolean logRawContent;

    private String regex;

    private boolean isActive;

    public boolean isLogRawContent() {
        return logRawContent;
    }

    public void setLogRawContent(boolean logRawContent) {
        this.logRawContent = logRawContent;
    }

    public String getRegexMatcherId() {
        return String.valueOf(regexMatcherId);
    }

    public void setRegexMatcherId(Long regexMatcherId) {
        this.regexMatcherId = regexMatcherId;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getRegexMatcherName() {
        return regexMatcherName;
    }

    public void setRegexMatcherName(String regexMatcherName) {
        this.regexMatcherName = regexMatcherName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public static PatternMatcherConfig createPatternMatcher(String regexMatcherName, String regex, boolean logRawContent, boolean isActive){
        PatternMatcherConfig patternMatcherConfig = new PatternMatcherConfig();
        patternMatcherConfig.regexMatcherName = regexMatcherName;
        patternMatcherConfig.regex = regex;
        patternMatcherConfig.logRawContent = logRawContent;
        patternMatcherConfig.isActive = isActive;
        return patternMatcherConfig;
    }

    public void update(PatternMatcherConfig patternMatcherConfig) {
        this.regexMatcherName = patternMatcherConfig.regexMatcherName;
        this.regex = patternMatcherConfig.regex;
        this.logRawContent = patternMatcherConfig.logRawContent;
        this.isActive = patternMatcherConfig.isActive;
        update();
    }
}
