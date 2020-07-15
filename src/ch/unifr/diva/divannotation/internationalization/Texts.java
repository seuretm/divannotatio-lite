/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation.internationalization;

import org.apache.log4j.Logger;

/**
 * @author liwicki
 */
public class Texts {

    public static final String DELETE_SELECTED_ZONE = "DELETE_SELECTED_ZONE";
    public static final String AUTO_GENERATE_RECT_LINES = "AUTO_GENERATE_RECT_LINES";
    public static final String AUTO_GENERATE_POLYG_LINES = "AUTO_GENERATE_POLYG_LINES";
    public static final String AUTO_GENERATE_DANIEL_LINES = "AUTO_GENERATE_DANIEL_LINES";
    public static final String AUTO_GENERATE_DANIEL_LINES2 = "AUTO_GENERATE_DANIEL_LINES2";
    public static final String CREATE_A_NEW_TEXT_AREA = "CREATE_A_NEW_TEXT_AREA";
    public static final String CREATE_A_NEW_DECORATION_AREA = "CREATE_A_NEW_DECORATION_AREA";
    public static final String CREATE_A_NEW_COMMENT_AREA = "CREATE_A_NEW_COMMENT_AREA";
    public static final String CREATE_A_NEW_TEXT_LINE_AREA = "CREATE_A_NEW_TEXT_LINE_AREA";
    public static final String CREATE_A_NEW_COMMENT_LINE_AREA = "CREATE_A_NEW_COMMENT_LINE_AREA";
    public static final String SELECT_ALL_AREAS_IN_RECTANGLE = "SELECT_ALL_AREAS_IN_RECTANGLE";
    public static final String CONVERT_TO_TEXT_AREA = "CONVERT_TO_TEXT_AREA";
    public static final String CONVERT_TO_DECORATION_AREA = "CONVERT_TO_DECORATION_AREA";
    public static final String CONVERT_TO_COMMENT_AREA = "CONVERT_TO_COMMENT_AREA";
    public static final String EXTRACT_OR_GROUP_AREAS = "EXTRACT_OR_GROUP_AREAS";
    public static final String USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION_POLY = "USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION_POLY";
    public static final String USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION = "USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION";
    public static final String DELETE_THE_CURRENTLY_SELECTED_AREAS = "DELETE_THE_CURRENTLY_SELECTED_AREAS";
    public static final String MAKE_A_NEW_TEXT_OR_COMMENT_AREA_OUT_OF_TH = "MAKE_A_NEW_TEXT_OR_COMMENT_AREA_OUT_OF_TH";
    public static final String CONVERT_SELECTED_AREAS_TO_TYPE_DECORATION = "CONVERT_SELECTED_AREAS_TO_TYPE_DECORATION";
    public static final String CONVERT_SELECTED_AREAS_TO_TYPE_COMMENT = "CONVERT_SELECTED_AREAS_TO_TYPE_COMMENT";
    public static final String CONVERT_SELECTED_AREAS_TO_TYPE_TEXT = "CONVERT_SELECTED_AREAS_TO_TYPE_TEXT";
    public static final String MULTISELECT_ALL_AREAS_OF_THE_TYPE = "MULTISELECT_ALL_AREAS_OF_THE_TYPE";
    public static final String CREATE_A_NEW_DECORATION_AREA_OUT = "CREATE_A_NEW_DECORATION_AREA_OUT";
    public static final String CREATE_A_NEW_COMMENT_LINE_AREA_OUT = "CREATE_A_NEW_COMMENT_LINE_AREA_OUT";
    public static final String CREATE_A_NEW_COMMENT_AREA_OUT = "CREATE_A_NEW_COMMENT_AREA_OUT";
    public static final String CREATE_A_NEW_TEXT_LINE_AREA_OUT = "CREATE_A_NEW_TEXT_LINE_AREA_OUT";
    public static final String CREATE_A_NEW_TEXT_AREA_OUT = "CREATE_A_NEW_TEXT_AREA_OUT";
    public static final String SPLIT_CURRENT_AREA = "SPLIT_CURRENT_AREA";
    public static final String SPLIT_CURRENT_AREA_INTO_SUB = "SPLIT_CURRENT_AREA_INTO_SUB";
    public static final String MERGE_SELECTED_AREAS = "MERGE_SELECTED_AREAS";
    public static final String SPLIT_CURRENT_AREA_TOOL = "SPLIT_CURRENT_AREA_TOOL";
    public static final String SPLIT_CURRENT_AREA_INTO_SUB_TOOL = "SPLIT_CURRENT_AREA_INTO_SUB_TOOL";
    public static final String MERGE_SELECTED_AREAS_TOOL = "MERGE_SELECTED_AREAS_TOOL";
    public static final String SPLIT_ZONE_HINT_MESSAGE = "SPLIT_ZONE_HINT_MESSAGE";
    public static final String SPLIT_ZONE_ERR_MESSAGE = "SPLIT_ZONE_ERR_MESSAGE";
    public static final String SPLIT_ZONE_ONLY_FOR_TWO = "SPLIT_ZONE_ONLY_FOR_TWO";
    public static final String SPLIT_ZONE_ERR_NOT_EMPTY = "SPLIT_ZONE_ERR_NOT_EMPTY";
    public static final String SPLIT_ZONE_ERR_NO_CHAR = "SPLIT_ZONE_ERR_NO_CHAR";
    private static final Logger logger = Logger.getLogger(Texts.class);
    private static LANGUAGE language = LANGUAGE.ENGLISH;

    private Texts() {

    }

    static void setLanguage(LANGUAGE l) {
        switch (l) {
            case ENGLISH:
                language = l;
                break;
            case FRENCH:
                language = l;
                break;
            case GERMAN:
                throw new UnsupportedOperationException("German is not supported yet");
            default:
                throw new UnsupportedOperationException("Language " + l + " is not recognized as language");
        }
    }

    public static String getText(String key) {
        String text = null;
        switch (language) {
            case ENGLISH:
                text = EnglishTexts.getText(key);
                break;
            case FRENCH:
                text = EnglishTexts.getText(key);
                if (text != null) {
                    break;
                }
            case GERMAN:
                break;
            default:
                text = EnglishTexts.getText(key);
                break;
        }
        if (null == text) {
            return key;
        }
        return text;
    }

    public enum LANGUAGE {
        ENGLISH, GERMAN, FRENCH
    }

}
