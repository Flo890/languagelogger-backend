package models.wordlists;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Word2CategoryMapping extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private String word;
    private String category;

    @ManyToOne
    private CategoryBaseList categoryBaseList;

    public Word2CategoryMapping(String word, String category, CategoryBaseList categoryBaseList) {
        this.word = word;
        this.category = category;
        this.categoryBaseList = categoryBaseList;
    }

    public Word2CategoryMapping() {
    }

    public String getWord() {
        return word;
    }

    public String getCategory() {
        return category;
    }
}
