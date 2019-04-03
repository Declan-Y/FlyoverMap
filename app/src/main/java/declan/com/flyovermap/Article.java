package declan.com.flyovermap;

public class Article {
    private  String title;
    private  String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

     Article(String title, String description){
        this.title = title;
        this.description = description;

    }
}
