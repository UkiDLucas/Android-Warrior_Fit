package com.cyberwalkabout.cyberfit.config.xml;

import com.cyberwalkabout.cyberfit.config.*;
import com.cyberwalkabout.cyberfit.model.v2.Author;
import com.cyberwalkabout.cyberfit.model.v2.Exercise;
import com.cyberwalkabout.cyberfit.model.v2.Program;
import com.google.common.base.Splitter;
import com.google.common.primitives.Longs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Andrii Kovalov
 */
@Component
public class XMLConfigReader implements ConfigReader {
    private static final Logger LOG = LoggerFactory.getLogger(XMLConfigReader.class);

    private XMLStreamReader createReader(InputStream in) throws ConfigReaderException {
        XMLStreamReader reader;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            reader = factory.createXMLStreamReader(in);
        } catch (XMLStreamException e) {
            throw new ConfigReaderException(e);
        }
        return reader;
    }

    @Override
    public Config readConfig(InputStream in) throws ConfigReaderException {

        XMLStreamReader reader = createReader(in);

        Config config = new Config();

        boolean inAuthor = false;
        boolean inProgram = false;
        boolean inExercise = false;

        Author author = null;
        Exercise exercise = null;
        Program program = null;

        String currentTag = null;

        try {
            while (reader.hasNext()) {
                int event = reader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT: {
                        currentTag = reader.getLocalName();

                        if (XMLConfigConst.TAG_ROOT.equals(currentTag)) {
                            String version = reader.getAttributeValue(0);

                            try {
                                config.setVersion(Integer.parseInt(version));
                            } catch (NumberFormatException e) {
                                LOG.warn("Couldn't read version", e);
                            }
                        } else if (XMLConfigConst.TAG_AUTHOR.equals(currentTag)) {
                            inAuthor = true;

                            author = new Author();

                            try {
                                author.setId(Long.parseLong(reader.getAttributeValue(0)));
                            } catch (NumberFormatException e) {
                                LOG.warn("Couldn't read author id", e);
                            }

                            config.addAuthor(author);
                        } else if (XMLConfigConst.TAG_PROGRAM.equals(currentTag)) {
                            inProgram = true;

                            program = new Program();

                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                QName qName = reader.getAttributeName(i);

                                if (XMLConfigConst.ATTR_ID.equals(qName.getLocalPart())) {
                                    try {
                                        program.setId(Long.parseLong(reader.getAttributeValue(i)));
                                    } catch (NumberFormatException e) {
                                        LOG.warn("Couldn't read program '" + XMLConfigConst.ATTR_ID + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_ACTIVE.equals(qName.getLocalPart())) {
                                    try {
                                        program.setActive(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read program '" + XMLConfigConst.ATTR_ACTIVE + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_PREMIUM.equals(qName.getLocalPart())) {
                                    try {
                                        program.setPremium(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read program '" + XMLConfigConst.ATTR_PREMIUM + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_AUTHOR_ID.equals(qName.getLocalPart())) {
                                    try {
                                        program.setAuthorId(Long.parseLong(reader.getAttributeValue(i)));
                                    } catch (NumberFormatException e) {
                                        LOG.warn("Couldn't read program '" + XMLConfigConst.ATTR_AUTHOR_ID + "' attr", e);
                                    }
                                }
                            }

                            config.addProgram(program);
                        } else if (XMLConfigConst.TAG_EXERCISE.equals(currentTag)) {
                            inExercise = true;
                            exercise = new Exercise();

                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                QName qName = reader.getAttributeName(i);

                                if (XMLConfigConst.ATTR_ID.equals(qName.getLocalPart())) {
                                    try {
                                        String id = reader.getAttributeValue(i);
                                        if (id != null && !id.isEmpty()) {
                                            exercise.setId(id);
                                        }
                                    } catch (NumberFormatException e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_ID + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_DISPLAY_ORDER.equals(qName.getLocalPart())) {
                                    try {
                                        exercise.setDisplayOrder(Integer.parseInt(reader.getAttributeValue(i)));
                                    } catch (NumberFormatException e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_DISPLAY_ORDER + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_ACTIVE.equals(qName.getLocalPart())) {
                                    try {
                                        exercise.setActive(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_ACTIVE + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_TRACK_DISTANCE.equals(qName.getLocalPart())) {
                                    try {
                                        exercise.setTrackDistance(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_TRACK_DISTANCE + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_TRACK_REPETITIONS.equals(qName.getLocalPart())) {
                                    try {
                                        exercise.setTrackRepetitions(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_TRACK_REPETITIONS + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_TRACK_TIME.equals(qName.getLocalPart())) {
                                    try {
                                        exercise.setTrackTime(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_TRACK_TIME + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_TRACK_WEIGHT.equals(qName.getLocalPart())) {
                                    try {
                                        exercise.setTrackWeight(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_TRACK_WEIGHT + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_YOUTUBE_ID.equals(qName.getLocalPart())) {
                                    try {
                                        exercise.setYoutubeId(reader.getAttributeValue(i));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_YOUTUBE_ID + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_IGNORE_YOUTUBE_TEXT.equalsIgnoreCase(qName.getLocalPart())) {
                                    try {
                                        exercise.setIgnoreYoutubeText(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_IGNORE_YOUTUBE_TEXT + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_ALIAS.equalsIgnoreCase(qName.getLocalPart())) {
                                    try {
                                        exercise.setName(reader.getAttributeValue(i));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_ALIAS + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_DESCRIPTION.equalsIgnoreCase(qName.getLocalPart())) {
                                    try {
                                        exercise.setDescription(reader.getAttributeValue(i));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_DESCRIPTION + "' attr", e);
                                    }
                                } else if (XMLConfigConst.ATTR_MAP.equalsIgnoreCase(qName.getLocalPart())) {
                                    try {
                                        exercise.setMapRequired(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_MAP + "' attr", e);
                                    }
                                } else if(XMLConfigConst.ATTR_TRACK_CALORIES.equalsIgnoreCase(qName.getLocalPart())) {
                                    try {
                                        exercise.setTrackCalories(Boolean.parseBoolean(reader.getAttributeValue(i)));
                                    } catch (Exception e) {
                                        LOG.warn("Couldn't read exercise '" + XMLConfigConst.ATTR_MAP + "' attr", e);
                                    }
                                }
                            }
                            if (exercise.getId() == null) {
                                exercise.setId(exercise.getYoutubeId());
                            }
                        } else if (inExercise && XMLConfigConst.TAG_ASSIGNED_PROGRAMS.equals(currentTag)) {
                            String programIdsString = reader.getAttributeValue(0);

                            Iterable<Long> programIds = Longs.stringConverter().convertAll(Splitter.on(",").trimResults().omitEmptyStrings().split(programIdsString));

                            config.addExercise(exercise, programIds);
                        }
                    }
                    break;
                    case XMLStreamConstants.CHARACTERS: {

                        String text = reader.getText();

                        if (text != null && !text.isEmpty()) {
                            text = text.trim();

                            if (inAuthor) {
                                if (XMLConfigConst.TAG_NAME.equals(currentTag)) {
                                    if (author.getName() != null) {
                                        text = author.getName() + text;
                                    }

                                    author.setName(text);
                                }
                            } else if (inProgram) {
                                if (XMLConfigConst.TAG_NAME.equals(currentTag)) {
                                    if (program.getName() != null) {
                                        text = program.getName() + text;
                                    }

                                    program.setName(text);
                                } else if (XMLConfigConst.TAG_DESCRIPTION.equals(currentTag)) {
                                    if (program.getDescription() != null) {
                                        text = program.getDescription() + text;
                                    }

                                    program.setDescription(text);
                                }
                            }
                        }
                    }
                    break;
                    case XMLStreamConstants.END_ELEMENT: {
                        currentTag = reader.getLocalName();
                        if (XMLConfigConst.TAG_AUTHOR.equals(currentTag)) {
                            inAuthor = false;
                        } else if (XMLConfigConst.TAG_PROGRAM.equals(currentTag)) {
                            inProgram = false;
                        } else if (XMLConfigConst.TAG_EXERCISE.equals(currentTag)) {
                            inExercise = false;
                        }
                    }
                    break;
                }
            }
        } catch (XMLStreamException e) {
            throw new ConfigReaderException(e);
        }


        return config;
    }
}
