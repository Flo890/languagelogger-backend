package models.wordlists;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Word extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private String word;

    @ManyToOne
    private WordBaseList wordBaseList;

    public Word(String word, WordBaseList wordBaseList) {
        this.word = word;
        this.wordBaseList = wordBaseList;
    }

    public Word() {
    }

    public String getWord() {
        return word;
    }
}
