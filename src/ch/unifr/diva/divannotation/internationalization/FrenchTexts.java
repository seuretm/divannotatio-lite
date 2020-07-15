/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divannotation.internationalization;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * FrenchText class of the LineSeg project
 *
 * @author Manuel Bouillon <manuel.bouillon@unifr.ch>
 *         date: 2016.09.09
 *         brief: french translations
 */
class FrenchTexts {

    private static final Logger logger = Logger.getLogger(FrenchTexts.class);

    private static final Map<String, String> TEXTS = new HashMap<>();

    static {
        TEXTS.put(Texts.DELETE_SELECTED_ZONE, "Supprimer la zone sélectionnée");
        TEXTS.put(Texts.AUTO_GENERATE_RECT_LINES, "Extraire automatiquement les lignes (rectangles)");
        TEXTS.put(Texts.AUTO_GENERATE_POLYG_LINES, "Extraire automatiquement les lignes (polygones)");
        TEXTS.put(Texts.CREATE_A_NEW_TEXT_AREA, "Creer une nouvelle zone de texte");
        TEXTS.put(Texts.CREATE_A_NEW_DECORATION_AREA, "Creer une nouvelle zone de décoration");
        TEXTS.put(Texts.CREATE_A_NEW_COMMENT_AREA, "Creer une nouvelle zone de commentaires");
        TEXTS.put(Texts.CREATE_A_NEW_TEXT_LINE_AREA, "Creer une nouvelle ligne de texte");
        TEXTS.put(Texts.CREATE_A_NEW_COMMENT_LINE_AREA, "Creer une nouvelle ligne de commentaire");
        TEXTS.put(Texts.SELECT_ALL_AREAS_IN_RECTANGLE, "Selectionner toutes les zones dans le rectangle");
        TEXTS.put(Texts.CONVERT_TO_TEXT_AREA, "Convertir en zone de texte");
        TEXTS.put(Texts.CONVERT_TO_DECORATION_AREA, "Convertir en zone de decoration");
        TEXTS.put(Texts.CONVERT_TO_COMMENT_AREA, "Convert en zone de commentaire");
        TEXTS.put(Texts.EXTRACT_OR_GROUP_AREAS, "Extraire ou grouper les zones");
        TEXTS.put(Texts.USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION_POLY, "Utiliser une methode d'extraction de ligne automatique de DIVAServices pour générer des lignes polygonales - Utilisable seulement pour les zones de texte et de commentaire.");
        TEXTS.put(Texts.USE_AN_AUTOMATIC_TEXT_LINE_SEGMENTATION, "Utiliser une methode d'extraction de ligne automatique de DIVAServices pour générer des lignes rectangulaires - Utilisable seulement pour les zones de texte et de commentaire.");
        TEXTS.put(Texts.DELETE_THE_CURRENTLY_SELECTED_AREAS, "Supprimer les zones sélectionnées - Peut également être effectué avec le boutton de suppression");
        TEXTS.put(Texts.MAKE_A_NEW_TEXT_OR_COMMENT_AREA_OUT_OF_TH, "Créer une nouvelle zone de texte ou de commentaire à partir des lignes de texte ou de commentaire sélectionnées - les supprime de la zone parente actuelle et supprime les zones parentes vides.");
        TEXTS.put(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_DECORATION, "Convertir les zones sélectionnées en zones de décoration - seulement appliquable aux zones de texte et de commentaires.");
        TEXTS.put(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_COMMENT, "Convertir les zones sélectionnées en zones de commentaire - seulement appliquable aux zones de texte et de décoration.");
        TEXTS.put(Texts.CONVERT_SELECTED_AREAS_TO_TYPE_TEXT, "Convertir les zones sélectionnées en zones de texte - seulement appliquable aux zones de commentaires et de décoration.");
        TEXTS.put(Texts.MULTISELECT_ALL_AREAS_OF_THE_TYPE, "Selectionnes toutes les zones du type indiqué ci-dessous qui sont dans le rectangle de sélection.");
        TEXTS.put(Texts.CREATE_A_NEW_DECORATION_AREA_OUT, "Créer une nouvelle zone de décoration à partir du rectangle ou polygone de sélection.");
        TEXTS.put(Texts.CREATE_A_NEW_COMMENT_LINE_AREA_OUT, "Créer une nouvelle ligne de commentaire à partir du rectangle ou polygone de selection.");
        TEXTS.put(Texts.CREATE_A_NEW_COMMENT_AREA_OUT, "Créer une nouvelle zone de commentaire à partir du rectangle ou polygone de selection.");
        TEXTS.put(Texts.CREATE_A_NEW_TEXT_LINE_AREA_OUT, "Créer une nouvelle ligne de texte à partir du rectangle ou polygone de selection.");
        TEXTS.put(Texts.CREATE_A_NEW_TEXT_AREA_OUT, "Créer une nouvelle zone de texte à partir du rectangle ou polygone de selection.");
        TEXTS.put(Texts.SPLIT_CURRENT_AREA, "Diviser la zone sélectionnée en deux.");
        TEXTS.put(Texts.SPLIT_CURRENT_AREA_INTO_SUB, "Diviser la zone sélectionnée en sous-catégories");
        TEXTS.put(Texts.MERGE_SELECTED_AREAS, "Fusionner les zones sélectionnées");
        TEXTS.put(Texts.SPLIT_CURRENT_AREA_TOOL, "Utiliser la ligne dessinée (deux points) pour générer deux zones du même type. Ne fonctionne seulement avec les zones sans sous-zones. Le milieu de la ligne est utilisé pour déterminer la zones à découper.");
        TEXTS.put(Texts.SPLIT_CURRENT_AREA_INTO_SUB_TOOL, "Utiliser la ligne dessinée (deux points) pour générer deux sous-zones du type enfant (texte - ligne - mot - charactère). Ne fonctionne seulement avec les zones sans sous-zones. Le milieu de la ligne est utilisé pour déterminer la zones à découper.");
        TEXTS.put(Texts.MERGE_SELECTED_AREAS_TOOL, "Fisionner les zones sélectionnée en une zone du même type.");
        TEXTS.put(Texts.SPLIT_ZONE_HINT_MESSAGE, "Dessiner une ligne avec exactement deux points pour effectuer une division.");
        TEXTS.put(Texts.SPLIT_ZONE_ERR_MESSAGE, "La ligne dessinée ne coupe pas de zone du type actuellement sélectionné.\nVérifier si le type sélectionné ci-dessous est correct.\nVérifier également que le milieu de la ligne est à l'intérieur de la zone à découper.");
        TEXTS.put(Texts.SPLIT_ZONE_ONLY_FOR_TWO, "La ligne dessinée doit croiser la zone à diviser en exactement deux points.");
        TEXTS.put(Texts.SPLIT_ZONE_ERR_NOT_EMPTY, "La zone ne peut pas être divisée car elle contient des sous-zones. Supprimer toutes les sous-zones ou sélectionner la sous-catégorie et diviser les sous-zones.");
        TEXTS.put(Texts.SPLIT_ZONE_ERR_NO_CHAR, "Les zones de type charactères ne peuvent pas être divisées en sous-zones. Elles peuvent par contre être diviser en deux zones de même type.");
    }

    static public String getText(String key) {
        return TEXTS.get(key);
    }

}
