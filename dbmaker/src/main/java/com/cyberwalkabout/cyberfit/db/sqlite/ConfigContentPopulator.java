package com.cyberwalkabout.cyberfit.db.sqlite;

import com.cyberwalkabout.cyberfit.config.Config;
import com.cyberwalkabout.cyberfit.db.DBContentPopulator;
import com.cyberwalkabout.cyberfit.db.DBException;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.AndroidMetadataTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.AuthorTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseToProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SelectedProgramTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SubscribedProgramTable;
import com.cyberwalkabout.cyberfit.model.v2.Author;
import com.cyberwalkabout.cyberfit.model.v2.Exercise;
import com.cyberwalkabout.cyberfit.model.v2.Program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @author Andrii Kovalov
 */
public class ConfigContentPopulator implements DBContentPopulator {
    public static final Logger LOG = LoggerFactory.getLogger(ConfigContentPopulator.class);

    private Config config;

    public ConfigContentPopulator(Config config) {
        this.config = config;
    }

    @Override
    public void populateDB(Connection connection) throws DBException {
        LOG.info("Populate database with data from config");

        populateMetadata(connection);
        populateAuthors(connection);
        populatePrograms(connection);
        populateExercises(connection);
        populateSelectedPrograms(connection);
        populateSubscribedPrograms(connection);
    }

    private void populateMetadata(Connection connection) {
        LOG.debug("Insert metadata");
        try {
            Statement statement = connection.createStatement();
            String sql = "insert into " + AndroidMetadataTable.TABLE_NAME + " values ('en_US')";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            LOG.error("Couldn't insert metadata into '" + AndroidMetadataTable.TABLE_NAME + "' table", e);
        }
    }

    private void populateSelectedPrograms(Connection connection) {
        LOG.debug("Insert selected programs (by default all selected)");
        try {
            Statement statement = connection.createStatement();
            String sql = "insert into " + SelectedProgramTable.TABLE_NAME + " (" + SelectedProgramTable.COLUMN_PROGRAM_ID + ") select " + ProgramTable.COLUMN_ID + " from " + ProgramTable.TABLE_NAME;
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            LOG.error("Couldn't insert program ids into '" + SelectedProgramTable.TABLE_NAME + "' table", e);
        }
    }

    private void populateSubscribedPrograms(Connection connection) {
        LOG.debug("Insert subscribed programs (by default all subscribed)");
        try {
            Statement statement = connection.createStatement();
            String sql = "insert into " + SubscribedProgramTable.TABLE_NAME + " (" + SubscribedProgramTable.COLUMN_PROGRAM_ID + ") select " + ProgramTable.COLUMN_ID + " from " + ProgramTable.TABLE_NAME;
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            LOG.error("Couldn't insert program ids into '" + SelectedProgramTable.TABLE_NAME + "' table", e);
        }
    }

    private void populateExercises(Connection connection) throws DBException {
        List<Exercise> exercises = config.getExercises();

        LOG.debug("Insert " + exercises.size() + " exercises");

        for (Exercise exercise : exercises) {
            insertExercise(exercise, connection);
        }

        Map<String, Iterable<Long>> exercisesToPrograms = config.getExercisesToPrograms();

        for (Map.Entry<String, Iterable<Long>> entry : exercisesToPrograms.entrySet()) {
            insertExerciseToProgramAssociation(entry.getKey(), entry.getValue(), connection);
        }
    }

    private void populatePrograms(Connection connection) {
        Map<Long, Program> programs = config.getPrograms();

        LOG.debug("Insert " + programs.size() + " programs");

        for (Program program : programs.values()) {
            insertProgram(program, connection);
        }
    }

    private void populateAuthors(Connection connection) {
        Map<Long, Author> authors = config.getAuthors();

        LOG.debug("Insert " + authors.size() + " authors");

        for (Author author : authors.values()) {
            insertAuthor(author, connection);
        }
    }

    private void insertExerciseToProgramAssociation(String exerciseId, Iterable<Long> programIds, Connection connection) throws DBException {
        String sql = "insert into " + ExerciseToProgramTable.TABLE_NAME +
                " ("
                + ExerciseToProgramTable.COLUMN_EXERCISE_ID + ", "
                + ExerciseToProgramTable.COLUMN_PROGRAM_ID
                + ") values (?,?)";

        for (Long programId : programIds) {
            try {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, exerciseId);
                    statement.setLong(2, programId);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                LOG.error("Couldn't insert entry to '" + ExerciseTable.TABLE_NAME + "' table", e);
                throw new DBException("Couldn't insert entry [" + exerciseId + ", " + programId + "] to '" + ExerciseTable.TABLE_NAME + "' table", e);
            }
        }
    }

    private void insertAuthor(Author author, Connection connection) {
        String sql = "insert into " + AuthorTable.TABLE_NAME +
                " ("
                + AuthorTable.COLUMN_ID + ", "
                + AuthorTable.COLUMN_NAME
                + ") values (?,?)";

        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, author.getId());
                statement.setString(2, author.getName());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.error("Couldn't insert entry to '" + AuthorTable.TABLE_NAME + "' table", e);
        }
    }

    private void insertProgram(Program program, Connection connection) {
        String sql = "insert into " + ProgramTable.TABLE_NAME +
                " ("
                + ProgramTable.COLUMN_ID + ", "
                + ProgramTable.COLUMN_AUTHOR_ID + ", "
                + ProgramTable.COLUMN_ACTIVE + ", "
                + ProgramTable.COLUMN_PREMIUM + ", "
                + ProgramTable.COLUMN_NAME + ", "
                + ProgramTable.COLUMN_DESCRIPTION
                + ") values (?,?,?,?,?,?)";

        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, program.getId());
                statement.setLong(2, program.getAuthorId());
                statement.setBoolean(3, program.isActive());
                statement.setBoolean(4, program.isPremium());
                statement.setString(5, program.getName());
                statement.setString(6, program.getDescription());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.error("Couldn't insert entry into '" + ProgramTable.TABLE_NAME + "' table", e);
        }
    }

    private void insertExercise(Exercise exercise, Connection connection) {
        String sql = "insert into " + ExerciseTable.TABLE_NAME +
                " ("
                + ExerciseTable.COLUMN_ACTIVE + ", "
                + ExerciseTable.COLUMN_DISPLAY_ORDER + ", "
                + ExerciseTable.COLUMN_YOUTUBE_ID + ", "
                + ExerciseTable.COLUMN_TRACK_DISTANCE + ", "
                + ExerciseTable.COLUMN_TRACK_REPETITIONS + ", "
                + ExerciseTable.COLUMN_TRACK_TIME + ", "
                + ExerciseTable.COLUMN_TRACK_WEIGHT + ", "
                + ExerciseTable.COLUMN_IGNORE_YOUTUBE_TEXT + ", "
                + ExerciseTable.COLUMN_NAME + ", "
                + ExerciseTable.COLUMN_DESCRIPTION + ", "
                + ExerciseTable.COLUMN_MAP_REQUIRED + ", "
                + ExerciseTable.COLUMN_ID + ", "
                + ExerciseTable.COLUMN_TRACK_CALORIES
                + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, exercise.isActive());
                statement.setInt(2, exercise.getDisplayOrder());
                statement.setString(3, exercise.getYoutubeId());
                statement.setBoolean(4, exercise.isTrackDistance());
                statement.setBoolean(5, exercise.isTrackRepetitions());
                statement.setBoolean(6, exercise.isTrackTime());
                statement.setBoolean(7, exercise.isTrackWeight());
                statement.setBoolean(8, exercise.isIgnoreYoutubeText());
                statement.setString(9, exercise.getName());
                statement.setString(10, exercise.getDescription());
                statement.setBoolean(11, exercise.isMapRequired());
                statement.setString(12, exercise.getId());
                statement.setBoolean(13, exercise.isTrackCalories());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.error("Couldn't insert entry into '" + ExerciseTable.TABLE_NAME + "' table\n" + exercise, e);
        }
    }
}
