package cse455.csusb.bookngo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TextbookAdapter extends RecyclerView.Adapter<TextbookAdapter.MyViewHolder> {

    private ArrayList<Textbook> textbooks;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView previewTitle, previewISBN, previewPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            previewTitle = itemView.findViewById(R.id.preview_title);
            previewISBN = itemView.findViewById(R.id.preview_isbn);
            previewPrice = itemView.findViewById(R.id.preview_price);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }
    }

    public TextbookAdapter(ArrayList<Textbook> textbooks) {
        this.textbooks = textbooks;
    }

    @Override
    public TextbookAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_textbook, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TextbookAdapter.MyViewHolder holder, int position) {
        holder.previewTitle.setText(textbooks.get(position).getTitle());
        holder.previewISBN.setText(textbooks.get(position).getIsbn());
        holder.previewPrice.setText(String.valueOf(textbooks.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return textbooks.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}