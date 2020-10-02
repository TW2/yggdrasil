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
package org.wingate.ygg.util.vlcjfx;

import java.awt.BorderLayout;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javax.swing.JPanel;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.util.FFStuffs;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 *
 * @author util2
 */
public class VLCjLogic extends JPanel {

    private final JFXPanel fxPanel = new JFXPanel();
    private String path = null;
    private FFStuffs ffss = null;
    
    private final ImageView videoImageView = new ImageView();
    private final MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
    private final EmbeddedMediaPlayer embeddedMediaPlayer = 
            mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
    
    private volatile Time limitTime = Time.create(0L);
    
    public VLCjLogic() {
        init();
    }
    
    private void init(){
        setLayout(new BorderLayout());
        add(fxPanel, BorderLayout.CENTER);
        
        Platform.runLater(() -> {
            videoImageView.setPreserveRatio(true);
            embeddedMediaPlayer.videoSurface().set(videoSurfaceForImageView(videoImageView));
            
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: gray;");

            videoImageView.fitWidthProperty().bind(root.widthProperty());
            videoImageView.fitHeightProperty().bind(root.heightProperty());

            root.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                // If you need to know about resizes
            });

            root.heightProperty().addListener((observableValue, oldValue, newValue) -> {
                // If you need to know about resizes
            });

            root.setCenter(videoImageView);
            
            Scene scene  = new Scene(root, 1200, 675, Color.BLACK);
            fxPanel.setScene(scene);
            
            embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter(){
                @Override
                public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                    if(Time.isEqual(limitTime, Time.create(0L)) == false){
                        if(Time.isGreater(limitTime, Time.create(newTime)) == true){
                            embeddedMediaPlayer.controls().stop();
                            limitTime = Time.create(0L);
                        }
                    }
                }
                
            });
        });
        
    }

    public EmbeddedMediaPlayer getEmbeddedMediaPlayer() {
        return embeddedMediaPlayer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path, FFStuffs ffss) {
        this.path = path;
        this.ffss = ffss;
    }
    
    public void free(){
        Platform.runLater(() -> {
            embeddedMediaPlayer.release();
            mediaPlayerFactory.release();
        });
    }
    
    public void seek(Time t){
        Platform.runLater(() -> {
            embeddedMediaPlayer.controls().play();
            embeddedMediaPlayer.controls().setTime(Time.toMillisecondsTime(t));
            embeddedMediaPlayer.controls().pause();
        });
    }
    
    public void play(){
        Platform.runLater(() -> {
            embeddedMediaPlayer.media().play(path);
        });
    }
    
    public void pause(){
        Platform.runLater(() -> {
            embeddedMediaPlayer.controls().pause();
        });
    }
    
    public void stop(){
        Platform.runLater(() -> {
            embeddedMediaPlayer.controls().stop();
        });
    }
    
    public void play(Time from, Time to){
        Platform.runLater(() -> {
            limitTime = to;
            embeddedMediaPlayer.media().play(path);
            embeddedMediaPlayer.controls().setTime(Time.toMillisecondsTime(from));
        });
    }
    
}
