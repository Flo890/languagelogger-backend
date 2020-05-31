package models.wordlists;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class WordBaseList extends Model {

    @Id
    @GeneratedValue
    private Long baselistId;
    private String baselistName;
    private String description;
    private String uploadedFilename;
    @OneToMany(mappedBy = "wordBaseList", cascade = CascadeType.ALL)
    private List<Word> words;

    public String getBaselistId() {
        return String.valueOf(baselistId);
    }

    public String getBaselistName() {
        return baselistName;
    }

    public String getDescription() {
        return description;
    }

    public String getUploadedFilename() {
        return uploadedFilename;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public String getWordCountString(){
        if (words == null){
            return "0";
        }
        Integer words = getWords().size();
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

    public static WordBaseList createWordBaselist(String baselistName, String description, String uploadedFilename){
        WordBaseList wordBaseList = new WordBaseList();
        wordBaseList.baselistName = baselistName;
        wordBaseList.description = description;
        wordBaseList.uploadedFilename = uploadedFilename;
        return wordBaseList;
    }

    public void update(WordBaseList baselist) {
        baselistName = baselist.baselistName;
        description = baselist.description;
        update();
    }

}
