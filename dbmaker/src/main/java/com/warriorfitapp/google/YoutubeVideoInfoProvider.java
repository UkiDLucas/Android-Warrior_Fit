package com.warriorfitapp.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Iterables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Andrii Kovalov
 */
public class YoutubeVideoInfoProvider {
    public static final Logger LOG = LoggerFactory.getLogger(YoutubeVideoInfoProvider.class);

    public static final String CONSUMER_NAME = "cyberfit-dbmaker";
    private final YouTube youtube;

    public YoutubeVideoInfoProvider(Credential credential) {
        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(CONSUMER_NAME).build();
    }

    public Video getVideoById(String id) {
        Video video = null;

        YouTube.Videos videos = youtube.videos();
        try {
            VideoListResponse response = videos.list("snippet").set("id", id).execute();

            video = Iterables.getFirst(response.getItems(), null);
        } catch (IOException e) {
            LOG.error("Couldn't retrieve video info from youtube by id '" + id + "'");
        }

        LOG.debug("Retrieved video info from youtube:\n" + video);

        return video;
    }
}
