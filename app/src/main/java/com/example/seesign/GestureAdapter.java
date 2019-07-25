package com.example.seesign;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GestureAdapter extends RecyclerView.Adapter<GestureAdapter.GestureViewHolder> {

    private Context context;
    private int layout;
    private List<Gesture> gestureList;

    public GestureAdapter(Context context, List<Gesture> gestureList, int layout) {
        this.context = context;
        this.gestureList = gestureList;
        this.layout = layout;
    }

    @Override
    public GestureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, null);
        return new GestureViewHolder(view);
    }


    @Override
    public void onBindViewHolder(GestureViewHolder holder, int position) {
        Gesture gesture = gestureList.get(position);

        if (gesture.isBitmap()) {
            holder.gesture.setImageBitmap(gesture.getImage());
        } else {
            holder.gesture.setImageDrawable(context.getResources().getDrawable(gesture.getImageDrawable()));
        }
        holder.description.setText(gesture.getDescription());

    }

    @Override
    public int getItemCount() {
        return gestureList.size();
    }

    class GestureViewHolder extends RecyclerView.ViewHolder {
        ImageView gesture;
        TextView description;

        public GestureViewHolder(View view){
            super(view);

            gesture = view.findViewById(R.id.gesture_image);
            description = view.findViewById(R.id.gesture_text);
        }
    }
}
