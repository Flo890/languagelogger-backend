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

package models;

import com.avaje.ebean.Model;
import org.apache.commons.lang3.StringUtils;
import utils.DateConverter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class KeyboardUserConfig extends Model implements Comparable<KeyboardUserConfig>{

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private long startDate;

    private boolean hasSentLayoutMessage;

    @ManyToOne
    private KeyboardLayout layout;

    public KeyboardUserConfig(long startDate, KeyboardLayout layout){
        this.startDate = startDate;
        this.layout = layout;
    }

    public Long getId(){
        return id;
    }

    public long getStartDate(){
        return startDate;
    }

    public String getStartDateText(){
        return DateConverter.getDateTimeStamp(startDate);
    }

    public KeyboardLayout getLayout(){
        return layout;
    }

    public String getLayoutName(){
        return layout == null ? null : layout.getName();
    }

    public String getLayoutId(){
        return layout == null ? null : layout.getLayoutId();
    }

    public long getActiveInterval(long millis){
        return millis - startDate;
    }

    public boolean hasLayoutId(String layoutId){
        return getLayout() != null && StringUtils.equals(getLayout().getLayoutId(), layoutId);
    }

    public boolean hasInfoMessage() {
        return getLayout() != null && getLayout().hasInfoMessage();
    }

    public String getInfoTitle(){
        return getLayout() == null ? null : getLayout().getInfoTitle();
    }

    public String getInfoMessage(){
        return getLayout() == null ? null : getLayout().getInfoMessage();
    }

    public boolean hasSentLayoutMessage(){
        return hasSentLayoutMessage;
    }

    public void setLayoutMessageSent() {
        hasSentLayoutMessage = true;
    }

    @Override
    public int compareTo(KeyboardUserConfig o) {
        return startDate > o.startDate ? 1 : -1;
    }
}