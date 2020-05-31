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

package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;
import play.mvc.Http;

import javax.annotation.Nullable;
import java.io.IOException;

public class JsonUtils {

    @Nullable
    public static JsonNode jsonFromPostRequest(@Nullable Http.Request request){
        if (request == null) {
            Logger.warn("request is null");
            return null;
        }
        Http.RequestBody body = request.body();
        if (body == null) {
            Logger.warn("body is null");
            return null;
        }
        return body.asJson();
    }

    public static String getPrettyJson(String jsonText){
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonText, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            Logger.warn("json error", e);
            return "";
        }
    }

    public static String getMinifiedJson(String jsonText){
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonText, Object.class);
            return mapper.writeValueAsString(json);
        } catch (IOException e) {
            Logger.warn("json error", e);
            return jsonText;
        }
    }
}
