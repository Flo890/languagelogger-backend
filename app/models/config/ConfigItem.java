/*
 * Copyright (C) 2016 - 2018 ResearchIME Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ConfigItem {

    @JsonProperty("studyName")
    private String studyName;

    @JsonProperty("studyDuration")
    private long studyDuration;

    @JsonProperty("studyWelcomeTitle")
    private String studyWelcomeTitle;

    @JsonProperty("studyWelcomeMessage")
    private String studyWelcomeMessage;

    @JsonProperty("keyboards")
    private List<KeyboardConfigItem> keyboards;

    @JsonProperty("showName")
    private boolean showName;

    @JsonProperty("showEndDate")
    private boolean showEndDate;

    @JsonProperty("showStatistics")
    private boolean showStatistics;

    @JsonProperty("showKeyboardName")
    private boolean showKeyboardName;

    public String getStudyName(){
        return studyName;
    }

    public long getStudyDuration(){
        return studyDuration;
    }

    public String getWelcomeTitle(){
        return studyWelcomeTitle;
    }

    public String getWelcomeMessage(){
        return studyWelcomeMessage;
    }

    public boolean hasWelcomeText(){
        return StringUtils.isNotBlank(studyWelcomeTitle) || StringUtils.isNotBlank(studyWelcomeTitle);
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public void setStudyDuration(long studyDuration) {
        this.studyDuration = studyDuration;
    }

    public void setStudyWelcomeTitle(String studyWelcomeTitle) {
        this.studyWelcomeTitle = studyWelcomeTitle;
    }

    public void setStudyWelcomeMessage(String studyWelcomeMessage) {
        this.studyWelcomeMessage = studyWelcomeMessage;
    }

    public List<KeyboardConfigItem> getKeyboards(){
        return keyboards == null ? Collections.emptyList() : keyboards;
    }

    public void addKeyboard(KeyboardConfigItem item) {
        if(keyboards == null){
            keyboards = new LinkedList<>();
        }
        keyboards.add(item);
    }

    public boolean showName(){
        return showName;
    }

    public void setShowName(boolean showName){
        this.showName = showName;
    }

    public boolean showEndDate(){
        return showEndDate;
    }

    public void setShowEndDate(boolean showEndDate){
        this.showEndDate = showEndDate;
    }

    public boolean showStatistics(){
        return showStatistics;
    }

    public void setShowStatistics(boolean showStatistics){
        this.showStatistics = showStatistics;
    }

    public boolean showKeyboardName() {
        return showKeyboardName;
    }

    public void setShowKeyboardName(boolean showKeyboardName) {
        this.showKeyboardName = showKeyboardName;
    }
}