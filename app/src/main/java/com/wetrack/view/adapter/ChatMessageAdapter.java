package com.wetrack.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wetrack.BaseApplication;
import com.wetrack.R;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.EntityCallbackWithLog;
import com.wetrack.client.WeTrackClient;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.User;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class ChatMessageAdapter extends BaseAdapter {
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
    private static final DateTimeFormatter monthDayTimeFormatter = DateTimeFormat.forPattern("MM-dd HH:mm");
    private static final DateTimeFormatter yearMonthDayTimeFormatter =
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    private Context context;

    private List<ChatMessage> chatMessageList;
    private final String currentUsername;

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList, String currentUsername) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.currentUsername = currentUsername;
    }

    public void refresh(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.chat_message_item, parent, false);

        LinearLayout leftLayout = (LinearLayout) row.findViewById(R.id.left_layout);
        LinearLayout rightLayout = (LinearLayout) row.findViewById(R.id.right_layout);
        ChatMessage message = chatMessageList.get(position);
        ChatMessage lastMessage = position == 0 ? null : chatMessageList.get(position - 1);
        // Received message
        if (!message.getFromUsername().equals(currentUsername)) {
            TextView timestamp = (TextView) row.findViewById(R.id.timestamp);
            TextView content = (TextView) row.findViewById(R.id.left_msg);
            final ImageView portrait = (ImageView) row.findViewById(R.id.tv_userhead);

            if (lastMessage != null && lastMessage.getSendTime().plusMinutes(3).isAfter(message.getSendTime()))
                timestamp.setVisibility(View.GONE);
            else if (message.getSendTime().getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
                timestamp.setText(message.getSendTime().toString(timeFormatter));
            else if (message.getSendTime().getYear() == LocalDateTime.now().getYear())
                timestamp.setText(message.getSendTime().toString(monthDayTimeFormatter));
            else
                timestamp.setText(message.getSendTime().toString(yearMonthDayTimeFormatter));

            content.setText(message.getContent());
            rightLayout.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);

            portrait.setImageResource(R.drawable.portrait_boy);
            WeTrackClient.singleton().getUserPortrait(message.getFromUsername(), false, new EntityCallback<Bitmap>() {
                @Override
                protected void onReceive(Bitmap bitmap) {
                    portrait.setImageBitmap(bitmap);
                }
            });

            return row;
        }
        // Sent message
        else {
            TextView timestamp = (TextView) row.findViewById(R.id.timestamp);
            TextView content = (TextView) row.findViewById(R.id.right_msg);
            final ImageView portrait = (ImageView) row.findViewById(R.id.iv_userhead);

            if (lastMessage != null && lastMessage.getSendTime().plusMinutes(3).isAfter(message.getSendTime()))
                timestamp.setVisibility(View.GONE);
            else if (message.getSendTime().getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
                timestamp.setText(message.getSendTime().toString(timeFormatter));
            else if (message.getSendTime().getYear() == LocalDateTime.now().getYear())
                timestamp.setText(message.getSendTime().toString(monthDayTimeFormatter));
            else
                timestamp.setText(message.getSendTime().toString(yearMonthDayTimeFormatter));

            content.setText(message.getContent());
            if (message.isAcked())
                row.findViewById(R.id.pb_sending).setVisibility(View.GONE);
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.VISIBLE);

            portrait.setImageResource(R.drawable.portrait_boy);
            WeTrackClient.singleton().getUserPortrait(message.getFromUsername(), false, new EntityCallback<Bitmap>() {
                @Override
                protected void onReceive(Bitmap bitmap) {
                    portrait.setImageBitmap(bitmap);
                }
            });

            return row;
        }
    }
}
