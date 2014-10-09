package org.getalp.similarity.local;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.segmentation.Segmenter;
import org.getalp.segmentation.SpaceSegmenter;
import org.getalp.similarity.local.string.TverskiIndex;

import java.util.List;

public class TuningTest {
    public static void main(String[] args) {
        TverskiIndex ti = new TverskiIndex(0.1, 0.1, true, true, new ScaledLevenstein());

        String defA = "Pierre à eau, ancêtre de l’évier en faïence, dans laquelle les ménagères faisaient la " +
                "vaisselle et tous les lavages relatifs à la préparation de la cuisine\n";

        String defB = "Calcul, concrétion qui se forme dans les reins, dans la vésicule ou ailleurs dans le corps.";

        String defC = "brique Un million d'ancien francs français (dix mille nouveaux francs français, soit " +
                "1524 euros). Construction faite de briques ou d'un assemblage de briques. " +
                "Emballage parallélépipédique utilisé pour certaines denrées alimentaires " +
                "Bloc de pierre artificielle fabriqué avec de la terre argileuse pétrie, " +
                "moulée, séchée, cuite, et dont on se sert comme matériau de construction. " +
                "Éclats, des tessons, des fragments d'une chose cassée. Matériau ou objet" +
                " ayant la forme d'une brique. Famille de fromages français à base de lait " +
                "de brebis, de chèvre ou de vache, à pâte molle à croûte fleurie et souvent en " +
                "forme de brique. brique Couleur rouge fade tirant sur le brun. caillou Pierre de " +
                "petite dimension. Objectif, en référence à la matière minérale du verre ou la manière dont " +
                "les lentilles sont enchâssées dans le fût en métal comme une pierre précieuse l'est sur une bague. " +
                "Pierre précieuse. Glaçon. Expression du franc-parler du sud de la France. " +
                "Tête, crâne. Sucre. Nom donné à la Nouvelle-Calédonie par les descendants des colons " +
                "français. Personne têtue. Huître. Récif, rocher. pion Petite tresse faite sur l'encolure" +
                " d'un cheval avec les crins de sa crinière. Pièce de certains autres jeux, tels que les dames, " +
                "Othello, backgammon, etc... Pièce du jeu d'échecs placée initialement sur la " +
                "seconde rangée de l'échiquier dans chaque camp. Petite pièce cylindrique se positionnant" +
                " dans un trou et servant de référence. Voir aussi simbleau, centreur, locating." +
                " Surveillant d'établissement scolaire, dans le langage des écoliers. " +
                "Quelqu'un qui n'a pas vraiment d'importance dans une organisation quelconque. " +
                "pion Particule du type méson. pion Relatif à Lavoine, commune française située dans " +
                "le département de l'Allier.";

        Segmenter s = new SpaceSegmenter();

        List<String> segA = s.segment(defA);
        List<String> segB = s.segment(defB);
        List<String> segC = s.segment(defC);
        System.err.println("Incorrect="+ti.compute(segA,segC));
        System.err.println("Correct=" +ti.compute(segB,segC));
        System.err.println("Diff=" +Math.abs(ti.compute(segB, segC) - ti.compute(segA, segC)));
        System.err.println("Ratio=" +Math.abs(ti.compute(segB,segC)/ti.compute(segA,segC)));

    }
}
