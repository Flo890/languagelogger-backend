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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Sensor extends Model {


    @Id
    Long id;

    @ManyToOne
    public Event event;

    public int type;

    //currently, no android sensor delivers more than 6 values
    public Float value0;
    public Float value1;
    public Float value2;
    public Float value3;
    public Float value4;
    public Float value5;


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setValues(float[] values) {
        if (values.length > 0) value0 = values[0];
        if (values.length > 1) value1 = values[1];
        if (values.length > 2) value2 = values[2];
        if (values.length > 3) value3 = values[3];
        if (values.length > 4) value4 = values[4];
        if (values.length > 5) value5 = values[5];
    }
}