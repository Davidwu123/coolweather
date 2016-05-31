package com.coolweather.app.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coolweather.app.R;

import java.util.List;

/**
 * Created by huiyi on 2016/5/23.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    List<String> mListString;
    Context mgetContext;
    OnItemClickListener mOnItemClickListener;

    public HomeAdapter(Context mContext, List<String> mList) {
        this.mgetContext = mContext;
        this.mListString = mList;

    }





    /****************************/
    //自定义点击事件
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    /****************************/







    //用于删除某一个item
    public void removeData(int position) {
        mListString.remove(position);
        notifyItemRemoved(position);
    }

    @Override//加载条目布局
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mgetContext).
                inflate(R.layout.simple_list, viewGroup, false));
        return holder;
    }

    //将视图与数据进行绑定
    public void onBindViewHolder(final MyViewHolder holder, int i) {
        holder.mTextView.setText(mListString.get(i));
        //自定义监听事件
        if (mOnItemClickListener != null) {//用户触发点击事件
            //最底层还是调用其他控件的监听事件
            //单击
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //得到当前recyclerView点击的id
                    int pos = holder.getLayoutPosition();
                    //传给自定义的监听事件
                    mOnItemClickListener.onItemClick(holder.mTextView, pos);
                }
            });


            //长按,只写出框架
            holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //得到当前recyclerView点击的id
                    int pos = holder.getLayoutPosition();
                    //传给自定义的监听事件
                    mOnItemClickListener.onItemLongClick(holder.mTextView, pos);
                    return false;
                }
            });

        }


    }


    @Override
    public int getItemCount() {
        return mListString.size();
    }


    //inner Class
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.item_text);
        }
    }
}
