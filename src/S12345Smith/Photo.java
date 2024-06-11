package S12345Smith;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Photo implements Serializable {
    private String title;
    private List<String> tags;
    private Date date;
    private String description;

    public Photo(String title, List<String> tags, Date date, String description) {
        this.title = title;
        this.tags = tags;
        this.date = date;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
