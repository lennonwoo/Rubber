package com.lennonwoo.rubber.data.source.remote;

import android.content.Context;
import android.util.Log;

import com.lennonwoo.rubber.contract.MusicDataSourceContract;
import com.lennonwoo.rubber.data.model.local.Song;
import com.lennonwoo.rubber.data.model.remote.SongFact;
import com.lennonwoo.rubber.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class MusicRemoteDataSource implements MusicDataSourceContract.RemoteDataSource {

    public static final String TAG = MusicRemoteDataSource.class.getSimpleName();

    public static final String PREFIX = "http://www.songfacts.com/";
    public static final String SEARCH = "search-songs-1.php?";

    private volatile static MusicRemoteDataSource INSTANCE;

    private MusicRemoteDataSource(Context context) {
    }

    public static MusicRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MusicRemoteDataSource.class) {
                if (INSTANCE == null)
                    INSTANCE = new MusicRemoteDataSource(context);
            }
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<SongFact>> getSongFactList(Song song) {
        Log.d(TAG, Utils.getThreadId() + "");
        String originalName = song.getName();
        int bracketIndex = originalName.indexOf('(');
        int dashIndex = originalName.indexOf('-');
        String songName;
        if (bracketIndex != -1) {
            songName = originalName.substring(0, bracketIndex - 1).toLowerCase();
        } else if (dashIndex != -1) {
            songName = originalName.substring(0, dashIndex - 1).toLowerCase();
        } else {
            songName = originalName.toLowerCase();
        }
        String[] splitName = songName.split(" ");
        StringBuilder builder = new StringBuilder(PREFIX);
        builder.append(SEARCH);
        for (String s : splitName) {
            builder.append(s);
            builder.append("%20");
        }
        builder.delete(builder.length() - 3, builder.length());
        List<SongFact> songFactList = new ArrayList<>();
        Elements e = null;
        try {
            Document doc = Jsoup.connect(builder.toString())
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .post();
            Element link = doc.select("li:contains(" + song.getArtist() + ")").select("a").first();
            if (link != null) {
                String path = link.attr("abs:href");
                Document facts = Jsoup.connect(path)
                        .data("query", "Java")
                        .userAgent("Mozilla")
                        .cookie("auth", "token")
                        .timeout(3000)
                        .post();
                e = facts.select("div.inner");
            }
        } catch (IOException IOe) {
            Log.d("test", "NETWORK???");
        }
        if (e != null)
            for (Element review : e)
                songFactList.add(new SongFact(song.getName(), review.text()));
        return Observable.from(songFactList).toList();
    }

}
