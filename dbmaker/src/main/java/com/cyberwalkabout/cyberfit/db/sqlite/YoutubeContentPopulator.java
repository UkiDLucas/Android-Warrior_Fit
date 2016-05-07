package com.cyberwalkabout.cyberfit.db.sqlite;

import com.cyberwalkabout.cyberfit.db.DBContentPopulator;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseTable;
import com.cyberwalkabout.cyberfit.google.YoutubeVideoInfoProvider;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Andrii Kovalov
 */
public class YoutubeContentPopulator implements DBContentPopulator {
    public static final Logger LOG = LoggerFactory.getLogger(YoutubeContentPopulator.class);
    private YoutubeVideoInfoProvider youtubeVideoInfoProvider;

    public YoutubeContentPopulator(YoutubeVideoInfoProvider youtubeVideoInfoProvider) {
        this.youtubeVideoInfoProvider = youtubeVideoInfoProvider;
    }

    @Override
    public void populateDB(Connection connection) {
        LOG.info("Populate database with data from youtube.com");

        List<String> youtubeIds = queryExerciseYoutubeIds(connection);

        for (String youtubeId : youtubeIds) {
            Video video = youtubeVideoInfoProvider.getVideoById(youtubeId);
            updateVideo(video, connection);
        }
    }

    private List<String> queryExerciseYoutubeIds(Connection connection) {
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
        try {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("select " + ExerciseTable.COLUMN_YOUTUBE_ID + " from " + ExerciseTable.TABLE_NAME + " where " + ExerciseTable.COLUMN_IGNORE_YOUTUBE_TEXT + " != 1")) {
                    while (resultSet.next()) {
                        String youtubeId = resultSet.getString(ExerciseTable.COLUMN_YOUTUBE_ID);
                        if (!Strings.isNullOrEmpty(youtubeId)) {
                            listBuilder.add(youtubeId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOG.error("Couldn't retrieve youtube ids from database", e);
        }

        ImmutableList<String> list = listBuilder.build();

        LOG.debug("Retrieved " + list.size() + " youtube ids from '" + ExerciseTable.TABLE_NAME + "' table");

        return list;
    }

    private void updateVideo(Video video, Connection connection) {
        if (video != null) {
            String sql = "update " + ExerciseTable.TABLE_NAME + " set " + ExerciseTable.COLUMN_NAME + "=?, " + ExerciseTable.COLUMN_DESCRIPTION + "=? where " + ExerciseTable.COLUMN_YOUTUBE_ID + "=?";

            try {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    VideoSnippet snippet = video.getSnippet();

                    statement.setString(1, snippet.getTitle());
                    statement.setString(2, snippet.getDescription());
                    statement.setString(3, video.getId());

                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                LOG.error("Couldn't update exercise data", e);
            }
        } else {
            LOG.warn("Youtube video is NULL");
        }
    }

}
