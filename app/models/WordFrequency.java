package models;


import com.avaje.ebean.Model;

import javax.persistence.*;

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"word","user_uuid","logical_word_list_id"}))
@Entity
public class WordFrequency extends Model {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 190) // with utf8mb4 encoding one character needs up to 4 bytes, so with INNODB max. key length of 767 bytes we have to limit this property to 190 characters
    public String userUuid;

    @Column(length = 190) // with utf8mb4 encoding one character needs up to 4 bytes, so with INNODB max. key length of 767 bytes we have to limit this property to 190 characters
    private String word;

    @Column
    private Integer count;

    // No foreign real key because could cause sync trouble when a list gets deleted in the backend
    @Column
    private Long logicalWordListId;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getLogicalWordListId() {
        return logicalWordListId;
    }

    public void setLogicalWordListId(Long logicalWordListId) {
        this.logicalWordListId = logicalWordListId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
}

