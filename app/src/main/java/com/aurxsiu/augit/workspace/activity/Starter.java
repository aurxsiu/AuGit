package com.aurxsiu.augit.workspace.activity;



import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.aurxsiu.augit.R;
import com.aurxsiu.augit.workspace.PickUtils;
import com.aurxsiu.augit.workspace.service.FileService;
import com.aurxsiu.augit.workspace.service.GitService;

import java.io.File;

public class Starter extends Activity {
    private FileService fileService;
    private GitService gitService;
    public static String TAG = "Starter";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileService = new FileService(this);

        setContentView(R.layout.startelayout);

        TextView textView = findViewById(R.id.getFile);
        textView.setText(fileService.target);

        ListView listView = findViewById(R.id.listView);
        fileService.setListView(listView);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            fileService.getFile(listView,position,textView);
        });

        EditText editText = findViewById(R.id.edit_text);

        Button createGitRepositoryButton = findViewById(R.id.createGit);
        createGitRepositoryButton.setOnClickListener(v->{
            listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new String[0]));
            gitService = new GitService(this);
            String uri = editText.getText().toString();
            gitService.gitClone(uri,fileService.targetFile);
            createGitRepositoryButton.setVisibility(View.INVISIBLE);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
