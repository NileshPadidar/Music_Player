package com.example.mymusicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicplayer.databinding.ItemMusicBinding;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MyHolder> {
    Context context;
    public ArrayList<AudioModel> audioModelArrayList;
    ArrayList<AudioModel> filterList;


    public MusicListAdapter(Context context, ArrayList<AudioModel> arrayList) {
        this.audioModelArrayList = arrayList;
        this.context = context;
        this.filterList = arrayList;

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMusicBinding binding = ItemMusicBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        AudioModel audioModel = filterList.get(position);
        holder.binding.tvTitle.setText(audioModel.title);

        if (MyMediaPlayer.currentIndex == position){
            holder.binding.tvTitle.setTextColor(Color.parseColor("#CC2F2F"));
        }else {
            holder.binding.tvTitle.setTextColor(Color.parseColor("#000000"));
        }

        holder.binding.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMediaPlayer.getInstance().reset();
                for(int i=0;i<audioModelArrayList.size();i++) {
                    Log.e("TAG", "Index: "+i);
                    if (audioModelArrayList.get(i).title.equals(audioModel.title)) {
                        Log.e("TAG", "Index I: "+i);
                        MyMediaPlayer.currentIndex = i;
                        break;
                    }
                }
                MainActivity.TitleName = audioModel.title;
                Intent intent = new Intent(context,MusicPlayerActivity.class);
                intent.putExtra("List",audioModelArrayList);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filterList = audioModelArrayList;
                } else {
                    ArrayList<AudioModel> filteredList = new ArrayList<>();
                    for (AudioModel row : audioModelArrayList) {
                        if (row.title.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    filterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterList = (ArrayList<AudioModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        ItemMusicBinding binding;

        public MyHolder(@NonNull ItemMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
