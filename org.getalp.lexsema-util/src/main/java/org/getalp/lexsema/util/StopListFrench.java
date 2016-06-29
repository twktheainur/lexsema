package org.getalp.lexsema.util;

import java.util.HashSet;
import java.util.Set;

public final class StopListFrench 
{
    private static Set<String> stopwords;

    private StopListFrench()
    {

    }

    @SuppressWarnings("OverlyLongMethod")
    private static void loadStopWords()
    {
        stopwords = new HashSet<>();
        stopwords.add("à");
        stopwords.add("alors");
        stopwords.add("au");
        stopwords.add("aucuns");
        stopwords.add("aussi");
        stopwords.add("autre");
        stopwords.add("avant");
        stopwords.add("avec");
        stopwords.add("avoir");
        stopwords.add("bon");
        stopwords.add("car");
        stopwords.add("ce");
        stopwords.add("c'");
        stopwords.add("cela");
        stopwords.add("ces");
        stopwords.add("ceux");
        stopwords.add("chaque");
        stopwords.add("ci");
        stopwords.add("comme");
        stopwords.add("comment");
        stopwords.add("dans");
        stopwords.add("des");
        stopwords.add("du");
        stopwords.add("d'");
        stopwords.add("dedans");
        stopwords.add("dehors");
        stopwords.add("depuis");
        stopwords.add("devrait");
        stopwords.add("doit");
        stopwords.add("donc");
        stopwords.add("dos");
        stopwords.add("début");
        stopwords.add("elle");
        stopwords.add("elles");
        stopwords.add("en");
        stopwords.add("encore");
        stopwords.add("essai");
        stopwords.add("est");
        stopwords.add("et");
        stopwords.add("eu");
        stopwords.add("fait");
        stopwords.add("faites");
        stopwords.add("fois");
        stopwords.add("font");
        stopwords.add("hors");
        stopwords.add("ici");
        stopwords.add("il");
        stopwords.add("ils");
        stopwords.add("je");
        stopwords.add("j'");
        stopwords.add("juste");
        stopwords.add("la");
        stopwords.add("le");
        stopwords.add("l'");
        stopwords.add("les");
        stopwords.add("leur");
        stopwords.add("là");
        stopwords.add("ma");
        stopwords.add("maintenant");
        stopwords.add("mais");
        stopwords.add("mes");
        stopwords.add("mine");
        stopwords.add("moins");
        stopwords.add("mon");
        stopwords.add("mot");
        stopwords.add("même");
        stopwords.add("ni");
        stopwords.add("ne");
        stopwords.add("n'");
        stopwords.add("nommés");
        stopwords.add("notre");
        stopwords.add("nous");
        stopwords.add("ou");
        stopwords.add("où");
        stopwords.add("par");
        stopwords.add("parce");
        stopwords.add("pas");
        stopwords.add("peut");
        stopwords.add("peu");
        stopwords.add("plupart");
        stopwords.add("pour");
        stopwords.add("pourquoi");
        stopwords.add("quand");
        stopwords.add("que");
        stopwords.add("qu'");
        stopwords.add("quel");
        stopwords.add("quelle");
        stopwords.add("quelles");
        stopwords.add("quels");
        stopwords.add("qui");
        stopwords.add("sa");
        stopwords.add("sans");
        stopwords.add("ses");
        stopwords.add("seulement");
        stopwords.add("si");
        stopwords.add("sien");
        stopwords.add("son");
        stopwords.add("sont");
        stopwords.add("sous");
        stopwords.add("soyez");
        stopwords.add("sujet");
        stopwords.add("sur");
        stopwords.add("ta");
        stopwords.add("tandis");
        stopwords.add("tellement");
        stopwords.add("tels");
        stopwords.add("tes");
        stopwords.add("ton");
        stopwords.add("tous");
        stopwords.add("tout");
        stopwords.add("trop");
        stopwords.add("très");
        stopwords.add("tu");
        stopwords.add("voient");
        stopwords.add("vont");
        stopwords.add("votre");
        stopwords.add("vous");
        stopwords.add("vu");
        stopwords.add("ça");
        stopwords.add("étaient");
        stopwords.add("état");
        stopwords.add("étions");
        stopwords.add("été");
        stopwords.add("être");
    }

    /**
     * @param token the string to tested
     * @return {@code true} if the token is a stop word, {@code false} otherwise
     */
    public static boolean isStopWord(String token) 
    {
        //noinspection StaticVariableUsedBeforeInitialization
        if (stopwords == null) {
            loadStopWords();
        }
        return stopwords.contains(token.toLowerCase().trim());
    }

}
