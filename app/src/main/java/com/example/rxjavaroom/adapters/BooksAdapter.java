package com.example.rxjavaroom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rxjavaroom.R;
import com.example.rxjavaroom.model.Book;
import com.example.rxjavaroom.util.GlideApp;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final List<Book> bookList;

    public BooksAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_books, parent, false);
        return new CardViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final Book book = bookList.get(position);
        CardViewHolder holder = (CardViewHolder) viewHolder;

        holder.textTitle.setText(book.getTitle());
        holder.textAuthor.setText(book.getAuthor());
        holder.textDescription.setText(book.getDescription());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(book, position);
            }
        });

        // load the background image
        if (book.getImageUrl() != null) {
            GlideApp.with(context)
                    .load(book.getImageUrl())
                    .centerCrop()
                    .into(holder.imageBackground);
        }
    }

    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {
        return bookList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView textTitle;
        final TextView textAuthor;
        final TextView textDescription;
        final ImageView imageBackground;

        CardViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_books);
            textTitle = itemView.findViewById(R.id.text_books_title);
            textAuthor = itemView.findViewById(R.id.text_books_author);
            textDescription = itemView.findViewById(R.id.text_books_description);
            imageBackground = itemView.findViewById(R.id.image_background);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Book book, int position);
    }
}
