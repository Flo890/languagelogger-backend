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

package controllers;

import models.Event;
import models.Sensor;
import play.mvc.Result;

import java.util.Map;

/**
 * Created by ResearchIME Project on 10.12.16.
 */
public class EventController extends JsonPostController<Event> {

    @Override
    protected Class<Event> getClassType() {
        return Event.class;
    }

    public Result postEvents() {
        return processRequestArrayData("events");
    }

    @Override
    protected void saveObject(Event event) {
        Map<Integer, float[]> sensorMap = event.getSensors();
        for (int sensorType : sensorMap.keySet()) {
            Sensor sensor = new Sensor();
            sensor.setEvent(event);
            sensor.setType(sensorType);
            sensor.setValues(sensorMap.get(sensorType));
            event.addSensor(sensor);
        }
        super.saveObject(event);
    }
}
