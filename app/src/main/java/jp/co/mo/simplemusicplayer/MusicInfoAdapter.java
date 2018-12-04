package jp.co.mo.simplemusicplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

public class MusicInfoAdapter extends BaseAdapter {

    WeakReference<Activity> mActivity;
    List<MusicInfo> mList;

    public MusicInfoAdapter(Activity mActivity, List<MusicInfo> mList) {
        this.mActivity = new WeakReference(mActivity);
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.get().getLayoutInflater();
        View view = inflater.inflate(R.layout.music_list, null);
        MusicInfo mInfo = mList.get(position);

        ((TextView)view.findViewById(R.id.musicTitle)).setText(mInfo.getSongName());
        ((TextView)view.findViewById(R.id.singerName)).setText(mInfo.getArtistName());

        return view;
    }
}
