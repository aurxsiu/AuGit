package com.aurxsiu.augit.workspace.service;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.aurxsiu.augit.workspace.service.parent.AndroidService;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FooterLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitService extends AndroidService {
    private static final String TAG = "GitService";
    private File targetRepository;
    private Git git;
    public GitService(Activity activity) {
        super(activity);
    }
    private void getGit(){
        try {
            Repository existingRepo = new FileRepositoryBuilder()
                    .setGitDir(targetRepository)
                    .build();
            git = new Git(existingRepo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void createGit(File targetRepository){
        if(git != null){
            //todo 正确返回错误
            throw new RuntimeException();
        }
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            Repository repository = builder.setGitDir(new File("/my/git/directory"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();
            git = new Git(repository);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void commitAll(String commitMessage){
        getGit();
        AddCommand add = git.add();
        CommitCommand commit = git.commit();
        try {
            add.addFilepattern("*").call();
            commit.setMessage(commitMessage).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
        git.close();
    }

    public List<String> getLog(){
        getGit();
        try {
            Iterable<RevCommit> call = git.log().call();
            ArrayList<String> result = new ArrayList<>();
            for(RevCommit commit:call){
                result.add("Commit: " + commit.getName() +
                        " " + "Date: " + commit.getAuthorIdent().getWhen() +
                        " " + "Message: " + commit.getFullMessage());
            }
            git.close();
            return result;
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private static class cloneThread implements Runnable{
        private final String url;
        private final File targetRepository;
        public cloneThread(String url,File targetRepository){
            this.url = url;
            this.targetRepository = targetRepository;
        }
        @Override
        public void run() {
            synchronized (cloneThread.class){
                try {
                    Git git = Git.cloneRepository()
                            .setURI(url)
                            .setDirectory(targetRepository)
                            .call();
                    git.close();
                } catch (GitAPIException e) {
                    Log.w(TAG, "gitClone: "+e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void gitClone(String url,File targetRepository) {
        if (targetRepository.listFiles().length != 0) {
            //todo 完善处理流程
            throw new RuntimeException();
        }
        new Thread(new cloneThread(url,targetRepository)).start();
    }
}
