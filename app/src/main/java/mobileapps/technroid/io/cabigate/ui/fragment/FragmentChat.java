package mobileapps.technroid.io.cabigate.ui.fragment;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.helper.DatabaseHelper;
import mobileapps.technroid.io.cabigate.models.ChatMessage;
import mobileapps.technroid.io.cabigate.ui.adapter.ChatMessageAdapter;

public class FragmentChat extends Fragment {

    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;



    private ChatMessageAdapter mAdapter;
    private MyApplication app;
    private DatabaseHelper  databaseHelper;
    private ArrayList<ChatMessage> chatMessages;
    String senderid="dispatcher";

    public  static  FragmentChat newInstance(ChatMessage chatMessage) {

        Bundle args = new Bundle();
        args.putSerializable("chatmessage",chatMessage);
        args.putBoolean("ischatmessage", true);

        FragmentChat fragment = new FragmentChat();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_chat, container,
                false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
         databaseHelper=new DatabaseHelper(getActivity());



        app= MyApplication.getInstance();
        mListView = (ListView)rootView. findViewById(R.id.listView);
        mButtonSend = (Button)rootView. findViewById(R.id.btn_send);
        mEditTextMessage = (EditText)rootView. findViewById(R.id.et_message);
        chatMessages=new ArrayList<ChatMessage>();
        chatMessages=databaseHelper.getAllChat();
        mAdapter = new ChatMessageAdapter(getActivity(),chatMessages );
        mListView.setAdapter(mAdapter);

        if(mAdapter.getCount()>0)
            scrollMyListViewToBottom();


        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mEditTextMessage.setText("");
            }
        });
        if(getArguments()!=null)
            if(getArguments().getBoolean("ischatmessage",false)){

                mimicOtherMessage((ChatMessage)getArguments().getSerializable("chatmessage"));
            }

      return  rootView;
    }

    private void sendMessage(String message) {
       // chatMessages.size();

        ChatMessage chatMessage = new ChatMessage(false, true,app.myDriver.username,message,senderid ,databaseHelper.getDateTime());
        mAdapter.add(chatMessage);
        databaseHelper.createCatagory(chatMessage);

        try {
            app.apiManager.mSocketManager.sendMessage(senderid,message);
            scrollMyListViewToBottom();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void mimicOtherMessage(ChatMessage message) {
        if(!MyApplication.getInstance().isAppOnline){
            moveToFront();
        }
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        senderid=message.getSenderid();
        ChatMessage chatMessage = new ChatMessage(false, false,message.getName(),message.getContent(),message.getSenderid() ,databaseHelper.getDateTime());
        databaseHelper.createCatagory(message);
        mAdapter.add(chatMessage);
        scrollMyListViewToBottom();
    }


    @TargetApi(11)
    protected void moveToFront() {
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            final ActivityManager activityManager = (ActivityManager)getActivity(). getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

            for (int i = 0; i < recentTasks.size(); i++)
            {
                Log.d("Executed app", "Application executed : "
                        + recentTasks.get(i).baseActivity.toShortString()
                        + "\t\t ID: " + recentTasks.get(i).id + "");
                // bring to front
                if (recentTasks.get(i).baseActivity.toShortString().indexOf("mobileapps.technroid.io.cabigate") > -1) {
                    activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                }
            }
        }
    }

    private void scrollMyListViewToBottom() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }



}
