package mobileapps.technroid.io.cabigate.ui.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.models.ChatMessage;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;

    public ChatMessageAdapter(Context context, List<ChatMessage> data) {
        super(context, R.layout.item_mine_message, data);
    }

    @Override
    public int getViewTypeCount() {
        // my message, other message, my image, other image
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);

        if (item.isMine() && !item.isImage()) return MY_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return OTHER_MESSAGE;
        else if (item.isMine() && item.isImage()) return MY_IMAGE;
        else return OTHER_IMAGE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);





            TextView textView = (TextView) convertView.findViewById(R.id.text);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            textView.setText(getItem(position).getContent());
            tvDate.setText(getTimeAgo(getItem(position).getCreated()));

        } else if (viewType == OTHER_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            textView.setText(getItem(position).getContent());
            tvDate.setText(getTimeAgo(getItem(position).getCreated()));
        }




        return convertView;
    }



    CharSequence getTimeAgo(String datetime){
        final String NEW_FORMAT = "yyyy-MM-dd HH:mm:ss";
        DateFormat formatter = new SimpleDateFormat(NEW_FORMAT);
        try {
            Date d = formatter.parse(datetime);
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    d.getTime(),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            return timeAgo;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
