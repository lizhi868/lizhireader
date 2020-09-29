package com.lizhi.reader.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lizhi.reader.R;
import com.lizhi.reader.bean.FindKindBean;
import com.lizhi.reader.view.activity.ChoiceBookActivity;
import com.lizhi.reader.widget.recycler.expandable.OnRecyclerViewListener;

import java.util.ArrayList;
import java.util.List;

public class FindRightAdapter extends RecyclerView.Adapter<FindRightAdapter.DescHolder> {
    private List<?> data = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private StringBuffer stringBuffer;
    private OnRecyclerViewListener.OnItemLongClickListener onItemLongClickListener;

    public FindRightAdapter(Context context, OnRecyclerViewListener.OnItemLongClickListener onItemLongClickListener) {
        this.context = context;
        stringBuffer = new StringBuffer();
        this.onItemLongClickListener = onItemLongClickListener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<?> data) {
        this.data = data;
        getImage();
        notifyDataSetChanged();
    }

    public List<?> getData() {
        return data;
    }

    @NonNull
    @Override
    public DescHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DescHolder(inflater.inflate(R.layout.item_find2_childer_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DescHolder holder, int position) {
        FindKindBean kindBean = (FindKindBean) data.get(position);
        holder.ivIcon.setImageResource(kindBean.getIcon());
        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append(kindBean.getKindName());
        if (stringBuffer.length() > 2) {
            stringBuffer.insert(2, "\n");
        }
        holder.tvType.setText(stringBuffer);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void getImage() {
        for (int i = 0; i < data.size(); i++) {
            FindKindBean kindBean = (FindKindBean) data.get(i);
            if (TextUtils.equals("男生", kindBean.getGroup())) {
                switch (kindBean.getKindName()) {
                    case "玄幻":
                        kindBean.setIcon(R.mipmap.xuanhuan_m);
                        break;
                    case "奇幻":
                        kindBean.setIcon(R.mipmap.qihuan_m);
                        break;
                    case "武侠":
                        kindBean.setIcon(R.mipmap.wuxia_m);
                        break;
                    case "仙侠":
                        kindBean.setIcon(R.mipmap.xianxia_m);
                        break;
                    case "职场":
                        kindBean.setIcon(R.mipmap.zhichang_m);
                        break;
                    case "都市":
                        kindBean.setIcon(R.mipmap.dushi_m);
                        break;
                    case "历史":
                        kindBean.setIcon(R.mipmap.lishi_m);
                        break;
                    case "军事":
                        kindBean.setIcon(R.mipmap.junshi_m);
                        break;
                    case "游戏":
                        kindBean.setIcon(R.mipmap.youxi_m);
                        break;
                    case "竞技":
                        kindBean.setIcon(R.mipmap.jinji_m);
                        break;
                    case "科幻":
                        kindBean.setIcon(R.mipmap.kehuan_m);
                        break;
                    case "灵异":
                        kindBean.setIcon(R.mipmap.lingyi_m);
                        break;
                    case "同人":
                        kindBean.setIcon(R.mipmap.tongren_m);
                        break;
                    case "轻小说":
                        kindBean.setIcon(R.mipmap.qingxiaoshuo_m);
                        break;
                }
            } else {
                switch (kindBean.getKindName()) {
                    case "玄幻言情":
                        kindBean.setIcon(R.mipmap.xuanhuanqyanqing_f);
                        break;
                    case "仙侠奇缘":
                        kindBean.setIcon(R.mipmap.xianxiaqiyuan_f);
                        break;
                    case "现代言情":
                        kindBean.setIcon(R.mipmap.xiandaiyanqing_f);
                        break;
                    case "浪漫青春":
                        kindBean.setIcon(R.mipmap.langmanqingchun_f);
                        break;
                    case "古代言情":
                        kindBean.setIcon(R.mipmap.gudaiyanqing_f);
                        break;
                    case "悬疑灵异":
                        kindBean.setIcon(R.mipmap.xuanyilingyi_f);
                        break;
                    case "科幻空间":
                        kindBean.setIcon(R.mipmap.kehuankongjian_f);
                        break;
                    case "游戏竞技":
                        kindBean.setIcon(R.mipmap.youxijingji_f);
                        break;
                    case "Nの次元":
                        kindBean.setIcon(R.mipmap.nciyuan_f);
                        break;
                    case "纯爱言情":
                        kindBean.setIcon(R.mipmap.chunaiyanqing_f);
                        break;
                }
            }
        }
    }

    class DescHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvType;

        DescHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.iv_icon);
            tvType = view.findViewById(R.id.tv_type);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FindKindBean kindBean = (FindKindBean) data.get(getAdapterPosition());
                    Intent intent = new Intent(context, ChoiceBookActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("url", kindBean.getKindUrl());
                    intent.putExtra("title", kindBean.getKindName());
                    intent.putExtra("tag", kindBean.getTag());
                    context.startActivity(intent);
                }
            });
        }
    }
}

