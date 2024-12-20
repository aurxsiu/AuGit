package com.aurxsiu.augit.workspace.service;

import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.TestLooperManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aurxsiu.augit.R;
import com.aurxsiu.augit.workspace.service.parent.AndroidService;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FileService extends AndroidService {
    private static final String TAG = "FileService";
    public File targetFile;
    public String target;
    private String[] list;
    private File[] listFiles;
    public static final int GET_FILE_ACTIVITY_CODE = 1;
    private void setInfo(File targetFile){
        this.targetFile = targetFile;target = targetFile.getPath();
        listFiles = targetFile.listFiles();list = Arrays.stream(listFiles).map(v->{return v.getPath().substring(target.length());}).toArray(String[]::new);
    }
    public FileService(Activity activity) {
        super(activity);
        setInfo(Environment.getExternalStorageDirectory());
    }
    public boolean AccessCheck(){
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            activity.startActivity(intent);
        }
        return Environment.isExternalStorageManager();
    }
    public void setListView(ListView listView){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
    }
    private void setTextView(TextView textView){
        textView.setText(target);
    }
    public void getFile(ListView listView, int id, TextView textView){
        //todo check function
        if(!AccessCheck()){
            throw new RuntimeException();
        }

        setInfo(listFiles[id]);

        setListView(listView);
        setTextView(textView);
    }
}
