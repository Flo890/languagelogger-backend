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

package database;

import play.Logger;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.Collection;

public class DataSelector {

    private DataSelector(){}

    public static <T> void fillData(Connection connection, String query, DataObjectCreator<T> creator){
        fillListData(connection, null, query, creator);
    }

    public static <T> void fillListData(Connection connection, @Nullable Collection<T> data, String query, DataObjectCreator<T> creator){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            while(rs.next()){
                try {
                    T item = creator.initItem(rs);
                    if(item != null){
                        if(data != null){
                            data.add(item);
                        }
                    }
                } catch (SQLException e) {
                    Logger.error("error creating object from database", e);
                }
            }
        } catch (SQLException e) {
            Logger.error("SQL error", e);
        } finally {
           close(stmt, rs);
        }
    }

    private static void close(Statement stmt, ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                Logger.error("error closing result set", e);
            }
        }
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                Logger.error("error closing statement", e);
            }
        }
    }
}
