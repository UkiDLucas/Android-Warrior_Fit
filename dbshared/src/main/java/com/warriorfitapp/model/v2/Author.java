package com.warriorfitapp.model.v2;

/**
 * @author Andrii Kovalov
 */
public class Author {
    private Long id;
    private String name;
    // url to author official web site, youtube?

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
