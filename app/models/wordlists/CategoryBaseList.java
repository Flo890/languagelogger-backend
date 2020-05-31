package models.wordlists;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class CategoryBaseList extends Model {

    @Id
    @GeneratedValue
    private Long baselistId;
    private String baselistName;
    private String description;
    private String uploadedFilename;
    @OneToMany(mappedBy = "categoryBaseList", cascade = CascadeType.ALL)
    private List<Word2CategoryMapping> word2CategoryMappings;

    public String getBaselistName(){
        return baselistName;
    }

    public String getBaselistId() {
        return String.valueOf(baselistId);
    }

    public String getDescription() {
        return description;
    }

    public String getUploadedFilename() {
        return uploadedFilename;
    }

    public void setWord2CategoryMappings(List<Word2CategoryMapping> word2CategoryMappings) {
        this.word2CategoryMappings = word2CategoryMappings;
    }

    public List<Word2CategoryMapping> getWord2CategoryMappings() {
        return word2CategoryMappings;
    }

    public String getWordCountString(){
        Integer words = word2CategoryMappings.size();
        if (words<100){
            return String.valueOf(words);
        }
        else if(words<10000){
            return Double.valueOf(Math.floor(words/1000)).intValue()+","+Double.valueOf(Math.floor((words%1000)/100)).intValue()+"k";
        }
        else {
            return Double.valueOf(Math.floor(words/1000)).intValue()+"k";
        }
    }

    public static CategoryBaseList createCategoryBaselist(String baselistName, String description, String uploadedFilename){
        CategoryBaseList categoryBaseList = new CategoryBaseList();
        categoryBaseList.baselistName = baselistName;
        categoryBaseList.description = description;
        categoryBaseList.uploadedFilename = uploadedFilename;
        return categoryBaseList;
    }

    public void update(CategoryBaseList baselist) {
        baselistName = baselist.baselistName;
        description = baselist.description;
        update();
    }
}
