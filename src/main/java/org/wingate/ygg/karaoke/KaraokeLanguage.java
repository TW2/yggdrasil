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

/**
 *
 * @author util2
 */
public enum KaraokeLanguage {
    Romaji("Rômaji"),
    Hiragana("Hiragana"),
    Katakana("Katakana"),
    Kanji("Kanji"),
    Japanese("Japanese"),
    Bopomofo("Bopomofo"),
    TradChinese("Traditional Chinese"),
    SimpChinese("Simplified Chinese"),
    Korean("Korean"),
    English("English"),
    French("French"),
    Spanish("Spanish"),
    Deutch("Deutch"),
    Italian("Italian");
    
    String language;
    
    private KaraokeLanguage(String language){
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return language;
    }
    
}
