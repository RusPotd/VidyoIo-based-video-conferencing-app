package com.team.together.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.team.together.Models.PeopleList;
import com.team.together.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleListAdapter extends ArrayAdapter<PeopleList> {

    private LayoutInflater mInflater;
    private List<PeopleList> mUsers = null;
    private int layoutResource;
    private Context mContext;


    public PeopleListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PeopleList> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder{
        TextView username, about;
        CircleImageView profileImage;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.about = (TextView) convertView.findViewById(R.id.about_in_list_view);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.username.setText(getItem(position).getPhone_number());
        //holder.about.setText(getItem(position).getEmail());


        return convertView;
    }
}
