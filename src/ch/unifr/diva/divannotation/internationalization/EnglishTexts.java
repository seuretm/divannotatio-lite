/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation.internationalization;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liwicki
 */
class EnglishTexts {

    private static final Logger logger = Logger.getLogger(EnglishTexts.class);

    private static final Map<String, String> TEXTS = new HashMap<>();

    static {
        TEXTS.put(Texts.DELETE_SELECTED_ZONE, "Delete selected zone");
        TEXTS.put(Texts.AUTO_GENERATE_RECT_LINES, "Auto-Generate Rect. Lines");
        TEXTS.put(Texts.AUTO_GENERATE_POLYG_LINES, "Auto-Generate Polyg. Lines");
        TEXTS.put(Texts.AUTO_GENERATE_DANIEL_LINES, "Daniel's Polyg. Lines");
        TEXTS.put(Texts.AUTO_GENERATE_DANIEL_LINES2, "Daniel's Polyg. Lines Bis");
        TEXTS.put(Texts.CREATE_A_NEW_TEXT_AREA, "Create a new Text Area");
        TEXTS.put(Texts.CREATE_A_NEW_DECORATION_AREA, "Create a new Decoration Area");
        TEXTS.put(Texts.CREATE_A_NEW_COMMENT_AREA, "Create a new Comment Area");
        TEXTS.put(Texts.CREATE_A_NEW_TEXT_LINE_AREA, "Create a new Text Line Area");
        TEXTS.put(Texts.CREATE_A_NEW_COMMENT_LINE_AREA, "Create a new Comment Line Area");
        TEXTS.put(Texts.SELECT_ALL_AREAS_IN_RECTANGLE, "Select all areas in rectangle");
        TEXTS.put(Texts.CONVERT_TO_TEXT_AREA, "Convert to Text Area");
        TEXTS.put(Texts.CONVERT_TO_DECORATION_AREA, "Convert to Decoration Area");
        TEXTS.put(Texts.CONVERT_TO_COMMENT_AREA, "Convert to Comment Area");
        TEXTS.put(Texts.EXTRACT_OR_GROUP_AREAS, "Extract or Group Area(s)");
        TEXTS.put(Texts.USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION_POLY, "Use an automatic TextLine segmentation method from DIVAServices to generate text line rectangles - only applicable to Text and Comment areas.");
        TEXTS.put(Texts.USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION, "Use an automatic TextLine segmentation method from DIVAServices to generate text line rectangles - only applicable to Text and Comment areas.");
        TEXTS.put(Texts.DELETE_THE_CURRENTLY_SELECTED_AREAS, "Delete the currently selected area(s) - can also be done by pressing the delete button");
        TEXTS.put(Texts.MAKE_A_NEW_TEXT_OR_COMMENT_AREA_OUT_OF_TH, "Make a new Text or Comment Area out of the selected text or comment lines - removing them from the current parent area and deleting empty parents.");
        TEXTS.put(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_DECORATION, "Convert selected areas to type Decoration Area - only applicable to Comment Areas and Text areas.");
        TEXTS.put(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_COMMENT, "Convert selected areas to type Comment Area - only applicable to Text Areas and Decoration areas.");
        TEXTS.put(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_TEXT, "Convert selected areas to type Text Area - only applicable to Comment Areas and Decoration areas.");
        TEXTS.put(Texts.MULTISELECT_ALL_AREAS_OF_THE_TYPE, "Multi-select all areas of the type indicated below the imageView which are within the black drawn rectangle.");
        TEXTS.put(Texts.CREATE_A_NEW_DECORATION_AREA_OUT, "Create a new Decoration Area out of the drawn black rectangle or polygon.");
        TEXTS.put(Texts.CREATE_A_NEW_COMMENT_LINE_AREA_OUT, "Create a new Comment Line Area out of the drawn black rectangle or polygon.");
        TEXTS.put(Texts.CREATE_A_NEW_COMMENT_AREA_OUT, "Create a new Comment Area out of the drawn black rectangle or polygon.");
        TEXTS.put(Texts.CREATE_A_NEW_TEXT_LINE_AREA_OUT, "Create a new Text Line Area out of the drawn black rectangle or polygon.");
        TEXTS.put(Texts.CREATE_A_NEW_TEXT_AREA_OUT, "Create a new Text Area out of the drawn black rectangle or polygon.");
        TEXTS.put(Texts.SPLIT_CURRENT_AREA, "Split marked Area into Two.");
        TEXTS.put(Texts.SPLIT_CURRENT_AREA_INTO_SUB, "Split marked Area into Subcategory");
        TEXTS.put(Texts.MERGE_SELECTED_AREAS, "Merge slected Areas");
        TEXTS.put(Texts.SPLIT_CURRENT_AREA_TOOL, "Uses the drawn black line (2 points) to generate two areas which both have the same type. Works only on Areas without children. The middle of the drawn line is used to determine the area to be split.");
        TEXTS.put(Texts.SPLIT_CURRENT_AREA_INTO_SUB_TOOL, "Uses the drawn black line (2 points) to generate two areas which both have the sub-category of the split area (Text-TextLine-Word-Character). Works only on Areas without children. The middle of the drawn line is used to determine the area to be split.");
        TEXTS.put(Texts.MERGE_SELECTED_AREAS_TOOL, "Merge slected Areas into one area of the same type");
        TEXTS.put(Texts.SPLIT_ZONE_HINT_MESSAGE, "Draw a line with exactly two points to perform a split.");
        TEXTS.put(Texts.SPLIT_ZONE_ERR_MESSAGE, "The drawn line is not cutting an area of the currently selected type.\nCheck if the area type (below the image view) is correct.\nAlso check if the middle of the drawn line is inside the area to be split.");
        TEXTS.put(Texts.SPLIT_ZONE_ONLY_FOR_TWO, "The drawn line should intersect exactly at two linesegments of the area to be split.");
        TEXTS.put(Texts.SPLIT_ZONE_ERR_NOT_EMPTY, "The zone cannot be split as it contains children. Delete all childZones first or select the child category and split the childZones further.");
        TEXTS.put(Texts.SPLIT_ZONE_ERR_NO_CHAR, "Character zones cannot be split into sub-zones. Try splitting into two zones instead.");


    }

    private EnglishTexts() {
    }

    static public String getText(String key) {
        return TEXTS.get(key);
    }
}
