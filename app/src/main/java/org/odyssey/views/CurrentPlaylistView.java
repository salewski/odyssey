/*
 * Copyright (C) 2016  Hendrik Borghorst & Frederik Luetkes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.odyssey.views;

import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.odyssey.R;
import org.odyssey.adapter.CurrentPlaylistListViewAdapter;
import org.odyssey.models.TrackModel;
import org.odyssey.playbackservice.NowPlayingInformation;
import org.odyssey.playbackservice.PlaybackServiceConnection;

public class CurrentPlaylistView extends LinearLayout implements AdapterView.OnItemClickListener {

    private final ListView mListView;
    private final Context mContext;

    private CurrentPlaylistListViewAdapter mCurrentPlaylistListViewAdapter;

    private PlaybackServiceConnection mPlaybackServiceConnection;

    public CurrentPlaylistView(Context context) {
        this(context, null);
    }

    /**
     * Set up the layout of the view.
     */
    public CurrentPlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_current_playlist, this, true);

        // get listview
        mListView = (ListView) this.findViewById(R.id.current_playlist_listview);

        mListView.setOnItemClickListener(this);

        mContext = context;
    }

    /**
     * Set the PBSServiceConnection object.
     * This will create a new Adapter.
     */
    public void registerPBServiceConnection(PlaybackServiceConnection playbackServiceConnection) {
        mPlaybackServiceConnection = playbackServiceConnection;

        mCurrentPlaylistListViewAdapter = new CurrentPlaylistListViewAdapter(mContext, mPlaybackServiceConnection);

        mListView.setAdapter(mCurrentPlaylistListViewAdapter);

        // set the selection to the current track, so the list view will positioned appropriately
        try {
            mListView.setSelection(mPlaybackServiceConnection.getPBS().getCurrentIndex());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Play the selected track.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            mPlaybackServiceConnection.getPBS().jumpTo(position);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * The playlist has changed so update the view.
     */
    public void playlistChanged(NowPlayingInformation info) {
        mCurrentPlaylistListViewAdapter.updateState(info);
        // set the selection to the current track, so the list view will positioned appropriately
        mListView.setSelection(info.getPlayingIndex());
    }

    /**
     * Removes the selected track from the playlist.
     *
     * @param position The position of the track in the playlist.
     */
    public void removeTrack(int position) {
        try {
            mPlaybackServiceConnection.getPBS().dequeueTrackIndex(position);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Enqueue the selected track as next track in the playlist.
     *
     * @param position The position of the track in the playlist.
     */
    public void enqueueTrackAsNext(int position) {
        // save track
        TrackModel track = (TrackModel) mCurrentPlaylistListViewAdapter.getItem(position);

        // remove track from playlist
        removeTrack(position);

        try {
            // enqueue removed track as next
            mPlaybackServiceConnection.getPBS().enqueueTrack(track, true);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return the album key for the selected track.
     *
     * @param position The position of the track in the playlist.
     */
    public String getAlbumKey(int position) {
        TrackModel clickedTrack = (TrackModel) mCurrentPlaylistListViewAdapter.getItem(position);

        return clickedTrack.getTrackAlbumKey();
    }

    /**
     * Return the selected artist title for the selected track.
     *
     * @param position The position of the track in the playlist.
     */
    public String getArtistTitle(int position) {
        TrackModel clickedTrack = (TrackModel) mCurrentPlaylistListViewAdapter.getItem(position);

        return clickedTrack.getTrackArtistName();
    }
}
