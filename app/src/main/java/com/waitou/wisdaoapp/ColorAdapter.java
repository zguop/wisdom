package com.waitou.wisdaoapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.waitou.wisdom_impl.view.SquareRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * auth aboom
 * date 2019-06-10
 */
public class ColorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Integer> colors = new ArrayList<>();

    public ColorAdapter(List<Integer> colors) {
        this.colors = colors;
    }


    private View.OnClickListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        SquareRelativeLayout squareRelativeLayout = new SquareRelativeLayout(viewGroup.getContext());
        squareRelativeLayout.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ColorViewHolder(squareRelativeLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Integer integer = colors.get(i);
        viewHolder.itemView.setBackgroundColor(integer);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }


    private static class ColorViewHolder extends RecyclerView.ViewHolder {

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
