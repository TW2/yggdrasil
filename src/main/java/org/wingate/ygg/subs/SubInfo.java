/*
 * Copyright (C) 2021 util2
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
package org.wingate.ygg.subs;

import org.wingate.ygg.subs.webvtt.WebVTTSubInfo;
import org.wingate.ygg.subs.ves.VesSubInfo;
import org.wingate.ygg.subs.ssb.SsbSubInfo;
import org.wingate.ygg.subs.ssb.SsbTargetSubInfo;
import java.util.HashMap;
import java.util.Map;
import org.wingate.ygg.subs.ass.AssSubInfo;

/**
 *
 * @author util2
 */
public class SubInfo {
    
    private Map<AssSubInfo, String> assInfos = new HashMap<>();
    private Map<SsbSubInfo, String> ssbInfos = new HashMap<>();
    private Map<SsbTargetSubInfo, String> ssbTarget = new HashMap<>();
    private Map<WebVTTSubInfo, String> webvttInfos = new HashMap<>();
    private Map<VesSubInfo, String> vesInfos = new HashMap<>();

    public SubInfo() {
    }

    public Map<AssSubInfo, String> getAssInfos() {
        return assInfos;
    }

    public void setAssInfos(Map<AssSubInfo, String> assInfos) {
        this.assInfos = assInfos;
    }

    public Map<SsbSubInfo, String> getSsbInfos() {
        return ssbInfos;
    }

    public void setSsbInfos(Map<SsbSubInfo, String> ssbInfos) {
        this.ssbInfos = ssbInfos;
    }

    public Map<SsbTargetSubInfo, String> getSsbTarget() {
        return ssbTarget;
    }

    public void setSsbTarget(Map<SsbTargetSubInfo, String> ssbTarget) {
        this.ssbTarget = ssbTarget;
    }

    public Map<WebVTTSubInfo, String> getWebvttInfos() {
        return webvttInfos;
    }

    public void setWebvttInfos(Map<WebVTTSubInfo, String> webvttInfos) {
        this.webvttInfos = webvttInfos;
    }

    public Map<VesSubInfo, String> getVesInfos() {
        return vesInfos;
    }

    public void setVesInfos(Map<VesSubInfo, String> vesInfos) {
        this.vesInfos = vesInfos;
    }
    
}
