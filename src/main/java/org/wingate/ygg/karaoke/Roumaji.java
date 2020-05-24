/*
 * Copyright (C) 2020 util2
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
package org.wingate.ygg.karaoke;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author util2
 */
public class Roumaji {
    
    private static final String[] ROUMAJI_LIST = new String[]{
        "fyu", "tyu", "dsu", "tso", "tse", "tsi", "tsa", "che", "she",
        "cha", "chu", "cho", "nya", "nyu", "nyo", "hya", "hyu", "hyo",
        "bya", "byu", "byo", "pya", "pyu", "pyo", "mya", "myu", "myo",
        "rya", "ryu", "ryo", "kya", "kyu", "kyo", "gya", "gyu", "gyo",
        "sha", "shu", "sho", "tsu", "chi", "shi",
        
        "ka", "ki", "ku", "ke", "ko", "sa", "su", "se", "so", "ta", "te",
        "to", "na", "ni", "nu", "ne", "no", "ha", "hi", "fu", "he", "ho",
        "ma", "mi", "mu", "me", "mo", "ya", "yu", "yo", "ra", "ri", "ru",
        "re", "ro", "wa", "wi", "we", "wo", "ga", "gi", "gu", "ge", "go",
        "za", "ji", "zu", "ze", "zo", "da", "ji", "zu", "de", "do", "ba",
        "bi", "bu", "be", "bo", "pa", "pi", "pu", "pe", "po", "ja", "ju",
        "jo", "wi", "we", "wo", "va", "vi", "vu", "ve", "vo", "je", "ti",
        "tu", "di", "du", "fa", "fi", "fe", "fo", "zi",
        
        "a", "i", "u", "e", "o", "n", "b", "d", "f", "g", "h", "j", "k",
        "m", "p", "r", "s", "t", "v", "w", "y", "z", " ", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "0", ",", "!", "?"
    };

    public Roumaji() {
    }
    
    public static List<Syllable> getLimits(String sentence){
        List<Syllable> syls = new ArrayList<>();
        
        String delta = sentence.toLowerCase();
        
        while(delta.isEmpty() == false){
            for (String roman : ROUMAJI_LIST) {
                if(delta.startsWith(roman) == true){
                    syls.add(Syllable.create(roman));
                    delta = delta.substring(roman.length());
                    break;
                }
            }
        }
        
        return syls;
    }
}
