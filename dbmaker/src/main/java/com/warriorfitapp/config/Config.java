package com.warriorfitapp.config;

import com.warriorfitapp.model.v2.Exercise;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents initial configuration of the tool that will be used to create and populate sqlite db
 *
 * @author Andrii Kovalov
 */
public class Config {
    private int version;
    private Map<Long, com.warriorfitapp.model.v2.Program> programs;
    private Map<Long, com.warriorfitapp.model.v2.Author> authors;
    private List<Exercise> exercises;
    private Map<String, Iterable<Long>> exercisesToPrograms;

    public void addProgram(com.warriorfitapp.model.v2.Program program) {
        if (programs == null) {
            programs = new HashMap<>();
        }
        programs.put(program.getId(), program);
    }

    public void addAuthor(com.warriorfitapp.model.v2.Author author) {
        if (authors == null) {
            authors = new HashMap<>();
        }

        authors.put(author.getId(), author);
    }

    public void addExercise(Exercise exercise, Iterable<Long> programIds) {
        if (exercises == null) {
            exercises = new ArrayList<>();
        }

        if (exercisesToPrograms == null) {
            exercisesToPrograms = new HashMap<>();
        }

        exercises.add(exercise);
        exercisesToPrograms.put(exercise.getId(), programIds);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<Long, com.warriorfitapp.model.v2.Program> getPrograms() {
        return programs;
    }

    public void setPrograms(Map<Long, com.warriorfitapp.model.v2.Program> programs) {
        this.programs = programs;
    }

    public Map<Long, com.warriorfitapp.model.v2.Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Map<Long, com.warriorfitapp.model.v2.Author> authors) {
        this.authors = authors;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public Map<String, Iterable<Long>> getExercisesToPrograms() {
        return exercisesToPrograms;
    }

    public void setExercisesToPrograms(Map<String, Iterable<Long>> exercisesToPrograms) {
        this.exercisesToPrograms = exercisesToPrograms;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("version", version)
                .add("programs", programs)
                .add("authors", authors)
                .add("exercises", exercises)
                .add("exercisesToPrograms", exercisesToPrograms)
                .toString();
    }
}
