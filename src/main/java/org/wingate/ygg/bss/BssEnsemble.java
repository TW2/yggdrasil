/*
 * Copyright (C) 2024 util2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wingate.ygg.bss;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.AssEvent;
import org.wingate.ygg.ass.AssStyle;
import org.wingate.ygg.ass.AssTime;

/**
 *
 * @author util2
 */
public class BssEnsemble {
    
    private final ASS ass;
    private final AssTime t;
    private final int imageWidth;
    private final int imageHeight;

    private final List<AssEvent> events = new ArrayList<>();
    private final List<BssShape> shapes = new ArrayList<>(); 

    public BssEnsemble(ASS ass, AssTime t, int imageWidth, int imageHeight) {
        this.ass = ass;
        this.t = t;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        
        // On récupère toutes les lignes ayant un moment entre le début et
        // la fin du temps de la ligne. Et on les mets dans laz liste à traiter.
        getEventsAtTime();
        
        // On crée un paquet de formes.
        calcShapes();
    }
    
    private void getEventsAtTime(){
        long ms = AssTime.toMillisecondsTime(t);
        
        for(AssEvent ev : ass.getEvents()){
            long msStart = AssTime.toMillisecondsTime(ev.getStartTime());
            long msEnd = AssTime.toMillisecondsTime((ev.getEndTime()));
            
            if(msStart <= ms && ms < msEnd){
                events.add(ev);
            }
        }
    }
    
    private void calcShapes(){
        for(AssEvent ev : events){
            processing(ev);
        }
    }
    
    private void processing(AssEvent ev){
        // Prépare les variables réutilisables
        Pattern p; Matcher m;
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        long msStart = AssTime.toMillisecondsTime(ev.getStartTime());
        long msEnd = AssTime.toMillisecondsTime(ev.getEndTime());
        
        // On effectue un préfiltrage des balises
        String text = ev.getText().replace("}{", "");
        
        // On cherche le style du text par rapport à son style
        AssStyle style = ass.getStyles().get(ev.getStyle().getName());
        
        // On initialise un collecteur de tags
        Map<String, Object> currentTags = style.getTagsFromStyle();
        
        // On cherche la position exacte avec : a an pos move
        
        // On cherche une balise a
        int an = -1;
        if(text.contains("\\a")){
            p = Pattern.compile("\\\\a(\\d+)"); m = p.matcher(text);
            if(m.find()) an = aToAn(Integer.parseInt(m.group(1)));
        }
        
        // On cherche une balise an si an est -1
        if(an == -1 && text.contains("\\an")){
            p = Pattern.compile("\\\\an(\\d+)"); m = p.matcher(text);
            if(m.find()) an = Integer.parseInt(m.group(1));
        }
        
        // Si on n'a pas encore trouvé l'alignement par défaut on applique
        // le style par défaut.
        if(an == -1) an = style.getAlignment();
        // TODO overrides r style
        
        // On calcule la largeur totale de la phrase afin de bien positionner
        // le texte à l'écran.
        
        // Liste des advances
        final List<Double> advances = new ArrayList<>();
        // Liste des textes
        final List<String> strings = new ArrayList<>();

        // Dernière avant advance
        double lastStart = -1d;
        // Dernier caractère traité
        String lastChar = null;

        // On cherche des couples tags<>syllabe
        p = Pattern.compile("(?<tags>[^\\}]*)\\}(?<s>[^\\{]*)");
        m = p.matcher(text);

        // Pour chaque couple
        while(m.find()){
            // On stocke la syllabe 
            String s = m.group("s");
            strings.add(s);
            // On stocke les tags
            String tags = m.group("tags");
            
            // On cherche les changements de style
            currentTags = getTags(tags, currentTags);

            // On veut trouver des modificateurs d'espaces (les derniers dans la chaine)
            // Il peut être positif, nul ou négatif et s'exprime en pixel
            int fsp = 0;
            if(tags.contains("\\fsp")){
                Pattern pFSP = Pattern.compile("\\\\fsp(-?\\d+)");
                Matcher mFSP = pFSP.matcher(tags);
                while(mFSP.find()) fsp = Integer.parseInt(mFSP.group(1));
            }

            // On veut trouver des modificateurs de taille sur X (les derniers dans la chaine)
            // Il peut être positif ou nul et s'exprime en pourcentage 100 représentant 100%
            int fscx = 100;
            if(tags.contains("\\fscx")){
                Pattern pFSCX = Pattern.compile("\\\\fscx(\\d+\\.?\\d*)");
                Matcher mFSCX = pFSCX.matcher(tags);
                while(mFSCX.find()) fscx = Integer.parseInt(mFSCX.group(1));
            }

            // On veut trouver des modificateurs de taille de police (les derniers dans la chaine)
            // Il peut être positif ou nul et s'exprime en point
            double fs = style.getFontsize();
            if(tags.contains("\\fs")){
                Pattern pFS = Pattern.compile("\\\\fs(\\d+\\.?\\d*)");
                Matcher mFS = pFS.matcher(tags);
                while(mFS.find()) fs = Double.parseDouble(mFS.group(1));
            }

            // On veut trouver des modificateurs de famille de police (les derniers dans la chaine)
            String fn = style.getFontname();
            if(tags.contains("\\fn")){
                Pattern pFN = Pattern.compile("\\\\fn([^\\\\]+)");
                Matcher mFN = pFN.matcher(tags);
                while(mFN.find()) fn = mFN.group(1);
            }

            // On veut trouver des reset (les derniers dans la chaine)
            // Implicitement cela correspond au style de la ligne
            // Explicitement cela correspond au style mentionné dans le tag
            String reset = style.getName();
            if(tags.contains("\\r")){
                Pattern pR = Pattern.compile("\\\\r([^\\\\]*)");
                Matcher mR = pR.matcher(tags);
                while(mR.find()) reset = mR.group(1);
                reset = reset.isEmpty() ? style.getName() : ass.getStyles().get(reset).getName();
            }

            // On regroupe tous les tags pour notre mix salade
            AssStyle sty = ass.getStyles().get(reset);
            // On cherche une taille sur X
            sty.setFontname(fn);
            sty.setFontsize(fs);
            Font font = sty.getFont();

            if(lastStart == -1d && lastChar == null){
                // Pour chaque caratère en couple de deux faire :
                for(int i=0; i<s.length() - 1; i++){
                    // On récupère un caractère
                    String c = java.lang.Character.toString(s.charAt(i));
                    // On y applique une fonte
                    g.setFont(font);

                    if(lastStart == -1d && lastChar == null){
                        // Si c'est le tout premier caractère, son offset est à zéro
                        lastStart = 0;
                    }else{
                        // Si on est dans la première syllabe
                        // mais que l'on a dépassé le premier caractère

                        // On joint dernier caractère et caractère en cours
                        // (parce que la mesure est à corriger si on prend
                        // chaque caractère 1 à 1)
                        String notAlone = lastChar + c;
                        int[] chArray = new int[2];
                        // On compte la taille des caractères 1 à 1
                        for(int j=0; j<notAlone.length(); j++){
                            chArray[j] = g.getFontMetrics().charWidth(notAlone.charAt(j));
                        }

                        // On obtient une taille 1 à 1 que l'on divise en pourcentage
                        // par rapport à son tatal afin de le réutiliser
                        // pour les 2 caractères et trouver l'avancement exact
                        int totalChars = chArray[0] + chArray[1];
                        float percentChArray_0 = (float)totalChars / (float)chArray[0];

                        // On calcule la taille à 2 caractères
                        float totalTwoChars = g.getFontMetrics().stringWidth(notAlone);
                        // On applique le fscx (scale X)
                        totalTwoChars *= (fscx / 100f);
                        // On applique le fsp (spacing)
                        totalTwoChars += fsp;

                        // Il nous reste plus qu'à trouver le bon avancement (advance)
                        // pour avoir la bonne position
                        // (position brute non transformée par d'autres tags)
                        lastStart = percentChArray_0 == 0 ? 0 : totalTwoChars / percentChArray_0;
                        advances.add(lastStart);
                    }
                    lastChar = c;                        
                }
            }else{
                // Pour chaque caratère en couple de deux faire :
                for(int i=0; i<s.length() - 1; i++){
                    // On récupère un caractère
                    String c = java.lang.Character.toString(s.charAt(i));
                    // On y applique une fonte
                    g.setFont(font);

                    // Si on est dans la première syllabe
                    // mais que l'on a dépassé le premier caractère

                    // On joint dernier caractère et caractère en cours
                    // (parce que la mesure est à corriger si on prend
                    // chaque caractère 1 à 1)
                    String notAlone = lastChar + c;
                    int[] chArray = new int[2];
                    // On compte la taille des caractères 1 à 1
                    for(int j=0; j<notAlone.length(); j++){
                        chArray[j] = g.getFontMetrics().charWidth(notAlone.charAt(j));
                    }

                    // On obtient une taille 1 à 1 que l'on divise en pourcentage
                    // par rapport à son tatal afin de le réutiliser
                    // pour les 2 caractères et trouver l'avancement exact
                    int totalChars = chArray[0] + chArray[1];
                    float percentChArray_0 = (float)totalChars / (float)chArray[0];

                    // On calcule la taille à 2 caractères
                    float totalTwoChars = g.getFontMetrics().stringWidth(notAlone);
                    // On applique le fscx (scale X)
                    totalTwoChars *= (fscx / 100f);
                    // On applique le fsp (spacing)
                    totalTwoChars += fsp;

                    // Il nous reste plus qu'à trouver le bon avancement (advance)
                    // pour avoir la bonne position
                    // (position brute non transformée par d'autres tags)
                    lastStart = percentChArray_0 == 0 ? 0 : totalTwoChars / percentChArray_0;
                    advances.add(lastStart);

                    lastChar = c;                        
                }
            }
        }
        
        if(advances.isEmpty()){
            strings.add(text);
            
            // On regroupe tous les tags pour notre mix salade
            Font font = style.getFont();
            float fscx = style.getScaleX();
            float fsp = style.getSpacing();

            if(lastStart == -1d && lastChar == null){
                // Pour chaque caratère en couple de deux faire :
                for(int i=0; i<text.length() - 1; i++){
                    // On récupère un caractère
                    String c = java.lang.Character.toString(text.charAt(i));
                    // On y applique une fonte
                    g.setFont(font);

                    if(lastStart == -1d && lastChar == null){
                        // Si c'est le tout premier caractère, son offset est à zéro
                        lastStart = 0;
                    }else{
                        // Si on est dans la première syllabe
                        // mais que l'on a dépassé le premier caractère

                        // On joint dernier caractère et caractère en cours
                        // (parce que la mesure est à corriger si on prend
                        // chaque caractère 1 à 1)
                        String notAlone = lastChar + c;
                        int[] chArray = new int[2];
                        // On compte la taille des caractères 1 à 1
                        for(int j=0; j<notAlone.length(); j++){
                            chArray[j] = g.getFontMetrics().charWidth(notAlone.charAt(j));
                        }

                        // On obtient une taille 1 à 1 que l'on divise en pourcentage
                        // par rapport à son tatal afin de le réutiliser
                        // pour les 2 caractères et trouver l'avancement exact
                        int totalChars = chArray[0] + chArray[1];
                        float percentChArray_0 = (float)totalChars / (float)chArray[0];

                        // On calcule la taille à 2 caractères
                        float totalTwoChars = g.getFontMetrics().stringWidth(notAlone);
                        // On applique le fscx (scale X)
                        totalTwoChars *= (fscx / 100f);
                        // On applique le fsp (spacing)
                        totalTwoChars += fsp;

                        // Il nous reste plus qu'à trouver le bon avancement (advance)
                        // pour avoir la bonne position
                        // (position brute non transformée par d'autres tags)
                        lastStart = percentChArray_0 == 0 ? 0 : totalTwoChars / percentChArray_0;
                        advances.add(lastStart);
                    }
                    lastChar = c;                        
                }
            }else{
                // Pour chaque caratère en couple de deux faire :
                for(int i=0; i<text.length() - 1; i++){
                    // On récupère un caractère
                    String c = java.lang.Character.toString(text.charAt(i));
                    // On y applique une fonte
                    g.setFont(font);

                    // Si on est dans la première syllabe
                    // mais que l'on a dépassé le premier caractère

                    // On joint dernier caractère et caractère en cours
                    // (parce que la mesure est à corriger si on prend
                    // chaque caractère 1 à 1)
                    String notAlone = lastChar + c;
                    int[] chArray = new int[2];
                    // On compte la taille des caractères 1 à 1
                    for(int j=0; j<notAlone.length(); j++){
                        chArray[j] = g.getFontMetrics().charWidth(notAlone.charAt(j));
                    }

                    // On obtient une taille 1 à 1 que l'on divise en pourcentage
                    // par rapport à son tatal afin de le réutiliser
                    // pour les 2 caractères et trouver l'avancement exact
                    int totalChars = chArray[0] + chArray[1];
                    float percentChArray_0 = (float)totalChars / (float)chArray[0];

                    // On calcule la taille à 2 caractères
                    float totalTwoChars = g.getFontMetrics().stringWidth(notAlone);
                    // On applique le fscx (scale X)
                    totalTwoChars *= (fscx / 100f);
                    // On applique le fsp (spacing)
                    totalTwoChars += fsp;

                    // Il nous reste plus qu'à trouver le bon avancement (advance)
                    // pour avoir la bonne position
                    // (position brute non transformée par d'autres tags)
                    lastStart = percentChArray_0 == 0 ? 0 : totalTwoChars / percentChArray_0;
                    advances.add(lastStart);

                    lastChar = c;                        
                }
            }
        }
        
        // On ajoute toutes les advances afin d'avoir la taille totale
        double sizeX = 0;
        double sizeY = g.getFontMetrics().getHeight(); // TODO fscy
        for(double d : advances){
            sizeX += d;
        }
        
        // On peut maintenant calculer le point d'insert à l'aide de pos et move
        double posX = -1;
        double posY = -1;
        
        List<String> pos = searchForMultiple(text, "\\pos",
                "\\\\pos\\((\\d+\\.?\\d*),(\\d+\\.?\\d*)\\)");
        if(pos.isEmpty() == false && pos.size() == 2){
            posX = Double.parseDouble(pos.get(0));
            posY = Double.parseDouble(pos.get(1));
        }
        
        if(posX == -1){
            List<String> move = searchForMultiple(text, "\\move",
                "\\\\move\\((\\d+\\.?\\d*),(\\d+\\.?\\d*),(\\d+\\.?\\d*),(\\d+\\.?\\d*),?(\\d*),?(\\d*)");
            if(move.isEmpty() == false && move.size() >= 4){
                long msStartIn = msStart, msEndIn = msEnd;
                double x1 = posX, y1 = posY, x2 = posX, y2 = posY;
                switch(move.size()){
                    case 6 -> {
                        x1 = Double.parseDouble(move.get(0));
                        y1 = Double.parseDouble(move.get(1));
                        x2 = Double.parseDouble(move.get(2));
                        y2 = Double.parseDouble(move.get(3));
                        msStartIn = Long.parseLong(move.get(4)) + msStart;
                        msEndIn = Long.parseLong(move.get(5)) + msStart;
                    }
                    case 4 -> {
                        x1 = Double.parseDouble(move.get(0));
                        y1 = Double.parseDouble(move.get(1));
                        x2 = Double.parseDouble(move.get(2));
                        y2 = Double.parseDouble(move.get(3));
                    }
                }
                
                // On calcule le delta
                long ms = AssTime.toMillisecondsTime(t);                
                if(msStartIn <= ms && ms < msEndIn){
                    if(msStartIn != msEndIn){
                        double percent = (ms - msStartIn) / (msEndIn - msStartIn);
                        posX = (x2 - x1) * percent + x1;
                        posY = (y2 - y1) * percent + y1;
                    }                    
                }else if(msStartIn < ms){
                    posX = x1;
                    posY = y1;
                }else if(msEnd > ms){
                    posX = x2;
                    posY = y2;
                }
            }
        }
        
        // On combine posX posY, sizeX sizeY et an pour trouver un alignement par défaut
        // suivant les positions sans compter les marges
        
        // wL (with margin L) a un point d'offset à gauche donc de 0 (x = 0)
        int wL = ev.getMarginL() != 0 || (ev.getMarginL() == 0 && ev.getStyle().getMarginL() != 0) ?
                ev.getMarginL() : ev.getStyle().getMarginL();
        
        // wR a un point d'offset à droite donc de imageWidth (x = imageWidth)
        int wR = ev.getMarginR() != 0 || (ev.getMarginR() == 0 && ev.getStyle().getMarginR() != 0) ?
                imageWidth - ev.getMarginR() : imageWidth - ev.getStyle().getMarginR();
        
        // wT (top) a un point d'offset en haut donc de 0 (y = 0)
        int wT = ev.getMarginV() != 0 || (ev.getMarginV() == 0 && ev.getStyle().getMarginV() != 0) ?
                ev.getMarginV() : ev.getStyle().getMarginV();
        
        // wB (bottom) a un point d'offset en bas donc de imageHeight (y = imageHeight)
        int wB = ev.getMarginV() != 0 || (ev.getMarginV() == 0 && ev.getStyle().getMarginV() != 0) ?
                imageHeight - ev.getMarginV() : imageHeight - ev.getStyle().getMarginV();
        
        // Le texte doit tenir entre wL et wR pour respecter les marges si pas de surcharges
        // Calcul de la différence pour trouver le milieu
        int wX = wR - wL;
        
        // Sur X
        switch(an){
            case 1, 4, 7 -> {
                // On ne fait rien de surprenant, on n'a pas besoin de size (coté gauche)
                // Ici tout est bon sauf si pos == -1, alors on applique
                // la position aux marges du style ou de la ligne
                if(posX == -1){
                    if(wX > sizeX){
                        // Cas où le texte est moins long que la largeur (tout va bien)
                        posX = wL;
                    }else{
                        // Cas où on doit faire plus d'une ligne sur la hauteur
                        // car on a plus de largeur de texte que de place en largeur
                        posX = wL; // TODO faire plusieurs lignes
                    }
                }
            }
            case 2, 5, 8 -> {
                // Milieu -> size / 2
                // Si pos == -1, alors on applique la position aux marges du style ou de la ligne
                if(posX == -1){
                    if(wX > sizeX){
                        // Cas où le texte est moins long que la largeur (tout va bien)
                        posX = wL + (wX - sizeX) / 2;
                    }else{
                        // Cas où on doit faire plus d'une ligne sur la hauteur
                        // car on a plus de largeur de texte que de place en largeur
                        posX = wL; // TODO faire plusieurs lignes
                    }
                }else{
                    posX -= (sizeX / 2);
                }
            }
            case 3, 6, 9 -> {
                // Droite -> size
                // Si pos == -1, alors on applique la position aux marges du style ou de la ligne
                if(posX == -1){
                    if(wX > sizeX){
                        // Cas où le texte est moins long que la largeur (tout va bien)
                        posX = wR - sizeX;
                    }else{
                        // Cas où on doit faire plus d'une ligne sur la hauteur
                        // car on a plus de largeur de texte que de place en largeur
                        posX = wL; // TODO faire plusieurs lignes
                    }
                }else{
                    posX -= sizeX;
                }
            }            
        }
        
        // Sur Y
        switch(an){
            case 1, 2, 3 -> {
                // Si pos == -1, on applique wB
                if(posY == -1){
                    posY = wB;
                }else{
                    posY += sizeY;
                }                
            }
            case 4, 5, 6 -> {
                posY = posY == -1 ? sizeY / 2 : posY + (sizeY / 2);
            }
            case 7, 8, 9 -> {
                // On ne fait rien de surprenant, on n'a pas besoin de size (haut)
                // Ici tout est bon sauf si pos == -1, alors on applique
                // la position aux marges du style ou de la ligne
                if(posY == -1){
                    posY = wT;
                }
            }
        }
        
        // On transforme chaque strings (lettre) en chemin
        double xp = posX, wp;
        for(int i=0; i<strings.size(); i++){
            String s = strings.get(i);
            wp = advances.get(i);
            
            TextLayout layout = null;
            
            if(Character.isWhitespace(s.toCharArray()[0]) == false){
                layout = new TextLayout(
                        s,
                        ev.getStyle().getFont(),
                        g.getFontRenderContext()
                );
            }
            
            AffineTransform transform = new AffineTransform();
            transform.translate(xp, posY);
            
            Shape shape = layout == null ? null : layout.getOutline(transform);
            
            BssShape bsh = new BssShape(currentTags, shape, i, xp, posY, wp, sizeY);
            
            shapes.add(bsh);
            
            xp += wp;
        }

        g.dispose();
    }
    
    private int aToAn(int a){
        switch(a){
            case 1 -> { return 1; }
            case 2 -> { return 2; }
            case 3 -> { return 3; }
            case 5 -> { return 7; }
            case 6 -> { return 8; }
            case 7 -> { return 9; }
            case 9 -> { return 4; }
            case 10 -> { return 5; }
            case 11 -> { return 6; }
            default -> { return 2; }
        }
    }

    public static String searchForSimple(String s, String expression, String regex){
        String found = null;

        if(s.contains(expression)){
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(s);

            while(m.find()){
                found = m.group(1);
            }
        }

        return found;
    }

    public static List<String> searchForMultiple(String s, String expression, String regex){
        List<String> found = new ArrayList<>();

        if(s.contains(expression)){
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(s);

            while(m.find()){
                for(int i=0; i<m.groupCount(); i++){
                    found.add(m.group(i+1));
                }                    
            }
        }

        return found;
    }
    
    private Map<String, Object> getTags(String tags, Map<String, Object> curTags){
        Map<String, Object> x = curTags;
        int index = 0;
        for(Tag tag : Tag.values()){
            if(tags.startsWith(tag.getStartsWith(), index)){
                Pattern p = Pattern.compile(tag.getRegex());
                Matcher m = p.matcher(tags.substring(index));
                if(m.find()){
                    switch(tag){
                        case Alignment -> {
                            Object o = Integer.valueOf(m.group(1));
                            x.put(tag.getStartsWith(), o);
                        }
                        case OldAlignment -> {
                            Object o = aToAn(Integer.parseInt(m.group(1)));
                            x.put("\\an", o);
                        }
                        case OldTextColor, TextColor, KaraokeColor, OutlineColor, ShadowColor -> {
                            int b = Integer.parseInt(m.group(1).substring(0, 2), 16);
                            int g = Integer.parseInt(m.group(1).substring(2, 4), 16);
                            int r = Integer.parseInt(m.group(1).substring(4), 16);
                            Object o = new Color(r, g, b);
                            x.put(tag.getStartsWith(), o);
                        }
                        case Alpha, TextAlpha, KaraokeAlpha, OutlineAlpha, ShadowAlpha -> {
                            Object o = Integer.valueOf(m.group(1), 16);
                            x.put(tag.getStartsWith(), o);
                        }
                        case Bold, Italic, Underline, StrikeOut -> {
                            Object o = Integer.valueOf(m.group(1));
                            x.put(tag.getStartsWith(), o);
                        }
                        case BlurEdge, Blur, FontSize -> {
                            Object o = Double.valueOf(m.group(1));
                            x.put(tag.getStartsWith(), o);
                        }
                        case Border, Shadow, Spacing, Rotation, RotationX, RotationY, RotationZ -> {
                            Object o = Double.valueOf(m.group(1));
                            x.put(tag.getStartsWith(), o);
                        }
                        case FontName -> {
                            Object o = m.group(1);
                            x.put(tag.getStartsWith(), o);
                        }
                        case XBorder, YBorder, XShadow, YShadow -> {
                            Object o = Double.valueOf(m.group(1));
                            x.put(tag.getStartsWith(), o);
                        }
                        case ScaleXY, ScaleX, ScaleY, ShearX, ShearY -> {
                            Object o = Double.valueOf(m.group(1));
                            x.put(tag.getStartsWith(), o);
                        }
                        case OldKaraokeFill, Karaoke, KaraokeFill, KaraokeOutline -> {
                            Object o = Integer.valueOf(m.group(1));
                            x.put(tag.getStartsWith(), o);
                        }
                        case Reset -> {
                            if(m.groupCount() == 0){
                                x.put(tag.getStartsWith(), curTags.get(tag.getStartsWith()));
                            }else{
                                x.put(tag.getStartsWith(), m.group(1));
                            }
                        }
                        case Position, Movement -> {
                            // Ne rien faire, ils sont traités dans cette classe
                        }
                        case WrapStyle -> {
                            Object o = Integer.valueOf(m.group(1));
                            x.put(tag.getStartsWith(), o);
                        }
                        case RectClip, RectInvisibleClip -> {
                            double x1 = Double.parseDouble(m.group("x1"));
                            double y1 = Double.parseDouble(m.group("y1"));
                            double x2 = Double.parseDouble(m.group("x2"));
                            double y2 = Double.parseDouble(m.group("y2"));
                            Object o = new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
                            x.put(tag.getStartsWith(), o);
                        }
                        case Fade -> {
                            int[] fade = new int[2];
                            fade[0] = Integer.parseInt(m.group(1));
                            fade[1] = Integer.parseInt(m.group(2));
                            x.put(tag.getStartsWith(), fade);
                        }
                        case OldFade -> {
                            int[] fade = new int[7];
                            fade[0] = Integer.parseInt(m.group(1));
                            fade[1] = Integer.parseInt(m.group(2));
                            fade[2] = Integer.parseInt(m.group(3));
                            fade[3] = Integer.parseInt(m.group(4));
                            fade[4] = Integer.parseInt(m.group(5));
                            fade[5] = Integer.parseInt(m.group(6));
                            fade[6] = Integer.parseInt(m.group(7));
                            x.put(tag.getStartsWith(), fade);
                        }
                        case Origin -> {
                            double xa = Double.parseDouble(m.group("x"));
                            double ya = Double.parseDouble(m.group("y"));
                            x.put(tag.getStartsWith(), new Point2D.Double(xa, ya));
                        }
                        case DrawClip, DrawInvisibleClip -> {
                            String c = m.group("commands");
                            x.put(tag.getStartsWith(), getGeneralPathFrom(c));
                        }
                        case Animation -> {
                            // TODO
                        }
                    }
                    index += m.group(0).length();
                }
                
            }
        }
        return x;
    }
    
    public static GeneralPath getGeneralPathFrom(String commands){
        GeneralPath gp = new GeneralPath();
        
        int index = 0;
        boolean end = false;
        while(end == false){
            if(commands.trim().startsWith("m", index)){
                Pattern p = Pattern.compile("m\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)");
                Matcher m = p.matcher(commands.substring(index));
                if(m.find()){
                    double x = Double.parseDouble(m.group(1));
                    double y = Double.parseDouble(m.group(2));
                    gp.moveTo(x, y);
                    index += m.group(0).length();
                }
            }else if(commands.trim().startsWith("n", index)){
                Pattern p = Pattern.compile("n\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)");
                Matcher m = p.matcher(commands.substring(index));
                if(m.find()){
                    double x = Double.parseDouble(m.group(1));
                    double y = Double.parseDouble(m.group(2));
                    gp.moveTo(x, y);
                    index += m.group(0).length();
                }
            }else if(commands.trim().startsWith("l", index)){
                Pattern p = Pattern.compile("l\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)");
                Matcher m = p.matcher(commands.substring(index));
                if(m.find()){
                    double x = Double.parseDouble(m.group(1));
                    double y = Double.parseDouble(m.group(2));
                    gp.lineTo(x, y);
                    index += m.group(0).length();
                }
            }else if(commands.trim().startsWith("b", index)){
                Pattern p = Pattern.compile("b\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)");
                Matcher m = p.matcher(commands.substring(index));
                if(m.find()){
                    double x1 = Double.parseDouble(m.group(1));
                    double y1 = Double.parseDouble(m.group(2));
                    double x2 = Double.parseDouble(m.group(3));
                    double y2 = Double.parseDouble(m.group(4));
                    double x3 = Double.parseDouble(m.group(5));
                    double y3 = Double.parseDouble(m.group(6));
                    gp.curveTo(x1, y1, x2, y2, x3, y3);
                    index += m.group(0).length();
                }
            }

            if(index >= commands.length() - 1){
                end = true;
            }
        }
        
        return gp;
    }
    
    public List<BssShape> getShapes() {
        return shapes;
    }
    
}
