package declan.com.flyovermap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class ArticleActivity extends AppCompatActivity {
    TextView mTitleTextView;
    TextView mDescriptionTextView;
    ArrayList<Article> articles = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ImageButton imageButton;
    public static final String TAG = ArticleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Article detail");
        setContentView(R.layout.activity_article);
        Intent intent = getIntent();
        ArrayList<String> values = intent.getStringArrayListExtra("VALUES");
        int i = 0;
        String title = null;
        String description = null;
        while(i < values.size() - 1){

            if(i%2 ==0) {
                if(values.get(i) != null) {
                    title = values.get(i);
                    description = values.get(i+1);
                }
                Article article = new Article(title, description);
                articles.add(article);

            }


            i++;

        }

        recyclerView = findViewById(R.id.recyclerView);
        mTitleTextView = findViewById(R.id.title_textView);
        mDescriptionTextView = findViewById(R.id.desc_textView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getParent()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new Adapter(articles);
        recyclerView.setAdapter(adapter);

        if(articles != null) {
            mTitleTextView.setText(articles.get(0).getTitle());
            mDescriptionTextView.setText(articles.get(0).getDescription());
        }

        imageButton = findViewById(R.id.notifyFriend);
        imageButton.setOnClickListener( l -> {
            Intent reportIntent = new Intent(Intent.ACTION_SEND);
            reportIntent.setType("text/plain");
            reportIntent.putExtra(Intent.EXTRA_TEXT, mDescriptionTextView.getText());
            startActivity(reportIntent);

        });





    }
    private class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        private Article mArticle;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(l -> {
                mTitleTextView.setText(mArticle.getTitle());
                mDescriptionTextView.setText(mArticle.getDescription());


            });


            titleTextView = itemView.findViewById(R.id.title);

        }
        public void bindArticle(Article article){
            mArticle = article;
            titleTextView.setText(mArticle.getTitle());

        }

    }
    private class Adapter extends RecyclerView.Adapter<ViewHolder>{
        private List<Article> mArticles;
        public Adapter(List<Article> articles){

            mArticles = articles;


        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ArticleActivity.this);
            View view = layoutInflater.inflate(R.layout.article_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Article article = mArticles.get(position);
            holder.bindArticle(article);


        }

        @Override
        public int getItemCount() {
            return mArticles.size();

        }
    }
}
