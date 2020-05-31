package database;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import models.KeyboardLayout;
import models.wordlists.CategoryBaseList;
import models.wordlists.LogicalCategoryList;
import models.wordlists.LogicalWordList;
import models.wordlists.WordBaseList;
import play.Logger;

import java.util.*;

public class WordlistDatabase {

    public static List<CategoryBaseList> getCategoryBaselists(){
        List<CategoryBaseList> categoryBaseLists = Ebean.find(CategoryBaseList.class).findList();
        if (categoryBaseLists == null || categoryBaseLists.isEmpty()) {
            //categoryBaseLists = createFromConfig();
        }
        return categoryBaseLists;
    }

    public static List<LogicalCategoryList> getLogicalCategoryLists(){
        List<LogicalCategoryList> logicalCategoryLists = Ebean.find(LogicalCategoryList.class).findList();
        if (logicalCategoryLists == null || logicalCategoryLists.isEmpty()) {
            //categoryBaseLists = createFromConfig();
        }
        return logicalCategoryLists;
    }

    public static CategoryBaseList getCategoryBaselist(String baselistId) {
        if(baselistId == null){return null;}
        return Ebean.find(CategoryBaseList.class)
                .where().eq("baselist_id", baselistId)
                .findUnique();
    }

    public static LogicalCategoryList getLogicalCategoryList(String logicallistId) {
        if(logicallistId == null){return null;}
        return Ebean.find(LogicalCategoryList.class)
                .where().eq("logicallist_id", logicallistId)
                .findUnique();
    }

    public static boolean isCategoryBaselistDBEmpty(){
        return Ebean.find(CategoryBaseList.class).findRowCount() == 0;
    }

    public static boolean deleteCategoryBaselist(String id) {
        CategoryBaseList baselist = getCategoryBaselist(id);
        if(baselist != null){
            try{
                baselist.delete();
                return true;
            }catch (Exception e){
                Logger.warn("could not delete baselist", e);
            }
        }
        return false;
    }

    public static Map<String,Integer> getCategoriesWordCountForBaselist(String id) {
        if (id==null){return null;}

        try {
            Integer.parseInt(id);
        } catch(NumberFormatException e){
            throw new IllegalArgumentException("baselistId must be numeric");
        }

        Map<String,Integer> result = new HashMap<>();
        List<SqlRow> rows = Ebean.createSqlQuery("SELECT category, COUNT(word) FROM word2category_mapping WHERE category_base_list_baselist_id="+id+" GROUP BY category").findList();
        for(SqlRow row : rows){
            result.put(row.getString("category"), row.getInteger("COUNT(word)"));
        }
        return result;
    }


    public static boolean deleteLogicalCategoryList(String id) {
        LogicalCategoryList logicalCategoryList = getLogicalCategoryList(id);
        if(logicalCategoryList != null){
            try{
                logicalCategoryList.delete();
                return true;
            }catch (Exception e){
                Logger.warn("could not delete logical category list", e);
            }
        }
        return false;
    }

    public static boolean isLogicalCategoryListDBEmpty(){
        return Ebean.find(LogicalCategoryList.class).findRowCount() == 0;
    }

    // -------- Word (White) Lists

    public static List<WordBaseList> getWordBaselists(){
        List<WordBaseList> wordBaseLists = Ebean.find(WordBaseList.class).findList();
        if (wordBaseLists == null || wordBaseLists.isEmpty()) {
            //categoryBaseLists = createFromConfig();
        }
        return wordBaseLists;
    }

    public static List<LogicalWordList> getLogicalWordLists(){
        List<LogicalWordList> logicalWordLists = Ebean.find(LogicalWordList.class).findList();
        if (logicalWordLists == null || logicalWordLists.isEmpty()) {
            //categoryBaseLists = createFromConfig();
        }
        return logicalWordLists;
    }

    public static WordBaseList getWordBaselist(String baselistId) {
        if(baselistId == null){return null;}
        return Ebean.find(WordBaseList.class)
                .where().eq("baselist_id", baselistId)
                .findUnique();
    }

    public static LogicalWordList getLogicalWordList(String logicallistId) {
        if(logicallistId == null){return null;}
        return Ebean.find(LogicalWordList.class)
                .where().eq("logicallist_id", logicallistId)
                .findUnique();
    }

    public static boolean deleteLogicalWordList(String id) {
        LogicalWordList logicalWordList = getLogicalWordList(id);
        if(logicalWordList != null){
            try{
                logicalWordList.delete();
                return true;
            }catch (Exception e){
                Logger.warn("could not delete logical word list", e);
            }
        }
        return false;
    }

    public static boolean isLogicalWordListDBEmpty(){
        return Ebean.find(LogicalWordList.class).findRowCount() == 0;
    }

    public static boolean deleteWordBaselist(String id) {
        WordBaseList baselist = getWordBaselist(id);
        if(baselist != null){
            try{
                baselist.delete();
                return true;
            }catch (Exception e){
                Logger.warn("could not delete baselist", e);
            }
        }
        return false;
    }

    public static boolean isWordBaselistDBEmpty(){
        return Ebean.find(WordBaseList.class).findRowCount() == 0;
    }


}
