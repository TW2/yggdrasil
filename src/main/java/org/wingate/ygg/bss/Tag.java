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

/**
 *
 * @author util2
 */
public enum Tag {
    Reset("reset", "\\r", "\\\\r([^\\\\]*)"),
    Position("position", "\\pos", "\\\\pos\\((?<x>\\d+.*\\d*),(?<y>\\d+.*\\d*)\\)"),
    Movement("movement", "\\move", "\\\\move\\((?<x1>\\d+.*\\d*),(?<y1>\\d+.*\\d*),(?<x2>\\d+.*\\d*),(?<y2>\\d+.*\\d*),*(?<t1>\\d*),*(?<t2>\\d*)\\)"),
    Bold("bold", "\\b", "\\\\b(\\d+)"),
    Italic("italic", "\\i", "\\\\i(\\d+)"),
    Underline("underline", "\\u", "\\\\u(\\d+)"),
    StrikeOut("strikeout", "\\s", "\\\\s(\\d+)"),
    Border("border", "\\bord", "\\\\bord(\\d+.*\\d*)"),
    XBorder("xborder", "\\xbord", "\\\\xbord(\\d+.*\\d*)"),
    YBorder("yborder", "\\ybord", "\\\\ybord(\\d+.*\\d*)"),
    Shadow("shadow", "\\shad", "\\\\shad(\\d+.*\\d*)"),
    XShadow("xshadow", "\\xshad", "\\\\xshad(\\d+.*\\d*)"),
    YShadow("yshadow", "\\yshad", "\\\\yshad(\\d+.*\\d*)"),
    BlurEdge("bluredge", "\\be", "\\\\be(\\d+.*\\d*)"),
    Blur("blur", "\\blur", "\\\\blur(\\d+.*\\d*)"),
    FontName("fontname", "\\fn", "\\\\fn([^\\\\]+)"),
    FontSize("fontsize", "\\fs", "\\\\fs(\\d+.*\\d*)"),
    ScaleX("scalex", "\\fscx", "\\\\fscx(\\d+.*\\d*)"),
    ScaleY("scaley", "\\fscy", "\\\\fscy(\\d+.*\\d*)"),
    ScaleXY("scale", "\\fsc", "\\\\fsc(\\d+.*\\d*)"),
    RotationX("rotationx", "\\frx", "\\\\frx(\\d+.*\\d*)"),
    RotationY("rotationy", "\\fry", "\\\\fry(\\d+.*\\d*)"),
    RotationZ("rotationz", "\\frz", "\\\\frz(\\d+.*\\d*)"),
    Rotation("rotationz", "\\fr", "\\\\fr(\\d+.*\\d*)"),
    Spacing("spacing", "\\fsp", "\\\\fsp(\\d+.*\\d*)"),
    ShearX("shearx", "\\fax", "\\\\fax(\\d+.*\\d*)"),
    ShearY("sheary", "\\fay", "\\\\fay(\\d+.*\\d*)"),
    OldTextColor("textcolor", "\\c", "\\\\c&H([0-9A-Fa-f]{6})&?"),
    Alpha("alpha", "\\alpha", "\\\\alpha&H([0-9A-Fa-f]{2})&?"),
    TextColor("textcolor", "\\1c", "\\\\1c&H([0-9A-Fa-f]{6})&?"),
    KaraokeColor("karaokecolor", "\\2c", "\\\\2c&H([0-9A-Fa-f]{6})&?"),
    OutlineColor("outlinecolor", "\\3c", "\\\\3c&H([0-9A-Fa-f]{6})&?"),
    ShadowColor("shadowcolor", "\\4c", "\\\\4c&H([0-9A-Fa-f]{6})&?"),
    TextAlpha("textalpha", "\\1a", "\\\\1a&H([0-9A-Fa-f]{2})&?"),
    KaraokeAlpha("karaokealpha", "\\2a", "\\\\2a&H([0-9A-Fa-f]{2})&?"),
    OutlineAlpha("outlinealpha", "\\3a", "\\\\3a&H([0-9A-Fa-f]{2})&?"),
    ShadowAlpha("shadowalpha", "\\4a", "\\\\4a&H([0-9A-Fa-f]{2})&?"),
    OldAlignment("alignment", "\\a", "\\\\a(\\d+)"),
    Alignment("alignment", "\\an", "\\\\an(\\d+)"),
    OldKaraokeFill("karaokefill", "\\K", "\\\\K(\\d+)"),
    Karaoke("karaoke", "\\k", "\\\\k(\\d+)"),
    KaraokeFill("karaokefill", "\\\\kf", "\\kf(\\d+)"),
    KaraokeOutline("karaokeoutline", "\\\\ko", "\\ko(\\d+)"),
    WrapStyle("wrapstyle", "\\q", "\\\\q(\\d+)"),
    Origin("origin", "\\org", "\\\\org\\((?<x>\\d+.*\\d*),(?<y>\\d+.*\\d*)\\)"),
    Fade("fade", "\\fad", "\\\\fad\\((?<x>\\d+.*\\d*),(?<y>\\d+.*\\d*)\\)"),
    OldFade("fade", "\\fade", "\\\\fade\\((?<a1>\\d+),(?<a2>\\d+),(?<a3>\\d+),(?<t1>\\d+),(?<t2>\\d+),(?<t3>\\d+),(?<t4>\\d+)\\)"),
    Animation("animation", "\\t", "\\\\t\\((?<t1>\\d*),*(?<t2>\\d*),*(?<acc>\\d*.*\\d*),*(?<mod>.+)\\)"),
    RectClip("clip", "\\clip", "\\\\clip\\((?<x1>\\d+.*\\d*),(?<y1>\\d+.*\\d*),(?<x2>\\d+.*\\d*),(?<y2>\\d+.*\\d*)\\)"),
    RectInvisibleClip("invisibleclip", "\\\\iclip", "\\iclip\\((?<x1>\\d+.*\\d*),(?<y1>\\d+.*\\d*),(?<x2>\\d+.*\\d*),(?<y2>\\d+.*\\d*)\\)"),
    DrawClip("drawclip", "\\clip", "\\\\clip\\((?<commands>[mnlbspc\\s\\d\\.]+)\\)"),
    DrawInvisibleClip("drawinvisibleclip", "\\iclip", "\\\\iclip\\((?<commands>[mnlbspc\\s\\d\\.]+)\\)"),
    AssDraw("drawing", "\\p", "\\\\p(?<factor>\\d+)");
    
    String name;
    String startsWith;
    String regex;
    
    private Tag(String name, String startsWith, String regex){
        this.name = name;
        this.startsWith = startsWith;
        this.regex = regex;
    }

    public String getName() {
        return name;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public String getRegex() {
        return regex;
    }
    
}
