package com.dev.abhinav.movierater.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.abhinav.movierater.DetailActivity;
import com.dev.abhinav.movierater.R;

import java.util.ArrayList;
import java.util.List;

public class tt extends RecyclerView.Adapter<tt.MyViewHolder> {

    private Context context;
    private List<Movie> list;

    public tt(Context context, List<Movie> list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, rating;
        public ImageView iv;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        Movie clickedDataItem = list.get(pos);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("original_title", list.get(pos).getOriginalTitle());
                        intent.putExtra("poster_path", list.get(pos).getPosterPath());
                        intent.putExtra("overview", list.get(pos).getOverview());
                        intent.putExtra("vote_average", Double.toString(list.get(pos).getVoteAverage()));
                        intent.putExtra("release_date", list.get(pos).getReleaseDate());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }
            });
        }
    }

    @NonNull
    @Override
    public tt.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull tt.MyViewHolder holder, int position) {
        String[] col = {
                "abc",
                "def"
        };
        List<Movie> favList = new ArrayList<>();
        Movie movie = new Movie();
        favList.add(movie);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private void gg() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                list.clear();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }
}
