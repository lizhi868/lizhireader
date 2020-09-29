package com.lizhi.reader.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lizhi.reader.R;
import com.lizhi.reader.bean.FindKindGroupBean;
import com.lizhi.reader.widget.recycler.expandable.bean.RecyclerViewData;

import java.util.ArrayList;
import java.util.List;

public class FindLeftAdapter extends RecyclerView.Adapter<FindLeftAdapter.MyViewHolder> {
    private Context context;
    private int showIndex = 0;
    private List<RecyclerViewData> data = new ArrayList<>();
    private OnClickListener onClickListener;

    public FindLeftAdapter(Context context, OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
    }

    public void setData(List<RecyclerViewData> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void upShowIndex(int showIndex) {
        if (showIndex != this.showIndex) {
            int oldIndex = this.showIndex;
            this.showIndex = showIndex;
            notifyItemChanged(oldIndex);
            notifyItemChanged(this.showIndex);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_find_left, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {
        FindKindGroupBean groupBean = (FindKindGroupBean) data.get(i).getGroupData();
        myViewHolder.tvSourceName.setText(groupBean.getGroupName());
        if (i == showIndex) {
            myViewHolder.line.setVisibility(View.VISIBLE);
            myViewHolder.tvSourceName.setTextColor(ContextCompat.getColor(context,R.color.main_color));
        } else {
            myViewHolder.line.setVisibility(View.GONE);
            myViewHolder.tvSourceName.setTextColor(ContextCompat.getColor(context,R.color.color_202E3D));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvSourceName;
        private View line;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSourceName = itemView.findViewById(R.id.tv_source_name);
            line = itemView.findViewById(R.id.view_line);
            itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    int oldIndex = showIndex;
                    showIndex = getAdapterPosition();
                    notifyItemChanged(oldIndex);
                    notifyItemChanged(showIndex);
                    onClickListener.click(showIndex);
                }
            });
        }
    }

    public interface OnClickListener {
        void click(int pos);
    }

}
