package com.example.youcoolmusic2.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.youcoolmusic2.Obg.PlayListItem;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Obg.PlayListView;
import com.example.youcoolmusic2.R;
import java.util.ArrayList;
import static com.example.youcoolmusic2.App.SavePosition;

public class PlayListsAdapter extends RecyclerView.Adapter<PlayListsAdapter.ExampleViewHolder> {

    private Context context;
    private TextView count_items;
    private ArrayList<PlayListItem> list;
    private RecyclerView listVideo;
    private AlertDialog newDialog;
    private String sort;
    private LinearLayoutManager mLinearLayoutManager;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView name_play_list;
        Button b_delete_item_play_list;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            name_play_list = itemView.findViewById(R.id.name_play_list);
            b_delete_item_play_list = itemView.findViewById(R.id.b_delete_item_play_list);
        }
    }

    public PlayListsAdapter(Context context,ArrayList<PlayListItem> list,RecyclerView listVideo,TextView count_items,
                            AlertDialog newDialog, String sort, LinearLayoutManager mLinearLayoutManager){
        this.context=context;
        this.list=list;
        this.listVideo = listVideo;
        this.count_items = count_items;
        this.newDialog = newDialog;
        this.sort = sort;
        this.mLinearLayoutManager = mLinearLayoutManager;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, final int position) {
        PlayListItem item = list.get(position);
        holder.name_play_list.setText(item.getTitle());
        holder.name_play_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int play_list_id = item.getId();
                App.play_list_id = play_list_id;
                SavePosition(context);

                if(!PlayListView.showPlay_List(context,"",listVideo,count_items,mLinearLayoutManager)){
                    App.toast.showToas(context,context.getString(R.string.not_found_play_list),false);
                }
                newDialog.cancel();
            }
        });
        holder.b_delete_item_play_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getString(R.string.delete_play_list));
                dialog.setMessage(context.getString(R.string.you_exactly_want_delete_play_list));
                dialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        App.sql.delete(App.dataBase.TABLE_MORE_PLAY_LIST, App.dataBase.ID
                                + " = '"+item.getId()+"'",null);
                        int newPos = holder.getAdapterPosition();
                        if(!list.isEmpty()) {
                            list.remove(newPos);
                        }
                        notifyItemRemoved(newPos);
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog newDialog = dialog.create();
                newDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}