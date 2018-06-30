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

package org.gateshipone.odyssey.utils;


import android.content.Context;
import android.net.Uri;

import org.gateshipone.odyssey.models.FileModel;
import org.gateshipone.odyssey.models.TrackModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class M3UParser extends PlaylistParser {
    private static final String TAG = M3UParser.class.getSimpleName();


    public M3UParser(FileModel playlistFile) {
        super(playlistFile);
    }

    @Override
    public ArrayList<String> getFileURLsFromFile(Context context) {
        Uri uri = FormatHelper.encodeURI(mFile.getPath());

        InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        if (null == inputStream) {
            return new ArrayList<>();
        }

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        ArrayList<String> urls = new ArrayList<>();
        do {
            try {
                line = bufReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (line == null || line.startsWith("#")) {
                try {
                    line = bufReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            urls.add(line);
        } while (line != null);


        return urls;
    }
}
