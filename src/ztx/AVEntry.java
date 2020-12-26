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
package ztx;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Formatter;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import org.bytedeco.ffmpeg.avcodec.*;
import org.bytedeco.ffmpeg.avformat.*;
import org.bytedeco.ffmpeg.avutil.*;
import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import org.bytedeco.ffmpeg.swresample.SwrContext;
import org.bytedeco.javacpp.*;

import java.awt.Graphics2D;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytedeco.ffmpeg.swscale.SwsContext;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.DataLine;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import static org.bytedeco.ffmpeg.global.swresample.swr_convert;
import static org.bytedeco.ffmpeg.global.swresample.swr_alloc_set_opts;
import static org.bytedeco.ffmpeg.global.swresample.swr_free;
import static org.bytedeco.ffmpeg.global.swresample.swr_init;
import static org.bytedeco.ffmpeg.global.swscale.SWS_BILINEAR;
import static org.bytedeco.ffmpeg.global.swscale.sws_getContext;
import static org.bytedeco.ffmpeg.global.swscale.sws_scale;
import org.wingate.timelibrary.Time;
/**
 *
 * @author util2
 */
public class AVEntry extends JPanel {

    private File file = null, oldFile = null;
    private BufferedImage image = null;
    private final FFMpegProcess process = new FFMpegProcess();

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        if(image != null){
            g.drawImage(image, 0, 0, null);
        }
    }
    
    public AVEntry() {
        
    }
    
    public void refresh(BufferedImage image){
        this.image = image;
        repaint();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public void setInstant(Time start, Time stop){
        process.setStartAt(start);
        process.setStopAt(stop);
    }
    
    public void avPlay(){
        if (file != null && file.exists() == true){
            process.play();
        }
    }
    
    public void avPause(){
        if (file != null && file.exists() == true){
            process.pause();
        }
    }
    
    public void avStop(){
        if (file != null && file.exists() == true){
            process.stop();
        }
    }
    
    public void avFreeResources(){
        if (file != null && file.exists() == true){
            process.freeAllResources();
        }
    }
    
    public void avRealloc(){
        process.realloc();
    }
    
    public PlayType getPlayType() {
        return process.getType();
    }

    public void setPlayType(PlayType type) {
        process.setType(type);
    }
    
    private class FFMpegProcess implements Runnable {

        // <editor-fold defaultstate="collapsed" desc="FFMPEG Variables">
    
        //==========================================================================
        // FFMpeg 4.0 - Video and Audio
        //==========================================================================

        private AVFormatContext         pFormatCtx = new AVFormatContext(null);
        private AVDictionary            OPTIONS_DICT = null;
        private AVPacket                pPacket = new AVPacket();

        //==========================================================================
        // FFMpeg 4.0 - Audio
        //==========================================================================

        private AVCodec                 pAudioCodec;
        private AVCodecContext          pAudioCodecCtx;
        private List<StreamInfo>        audioStreams = new ArrayList<>();
        private int                     audio_data_size;
        private BytePointer             audio_data = null;
        private int                     audio_ret;
        private AVFrame                 pAudioFrame = null;
        private AVFrame                 pAudioDecodedFrame = null;
        private AVCodecParserContext    pAudioParser;
        private SwrContext              audio_swr_ctx = null;

        //==========================================================================
        // FFMpeg 4.0 - Video
        //==========================================================================

        private List<StreamInfo>        videoStreams = new ArrayList<>();
        private AVCodecContext          pVideoCodecCtx = null;
        private AVCodec                 pVideoCodec = null;
        private AVFrame                 pVideoFrame = null;
        private AVFrame                 pVideoFrameRGB = null;
        private BytePointer             video_buffer = null;
        private int                     video_numBytes;    
        private SwsContext              video_sws_ctx = null;

        // </editor-fold>
        
        private PlayType type = PlayType.VideoOnly;
        private Time start = Time.create(0L);
        private Time stop = Time.create(0L);
        private Thread th = null;
        private volatile boolean playing = false;
        private long startSystemTime = 0L;
        private long currentMediaTime = 0L;
        
        public FFMpegProcess() {
            init();
        }
        
        private void init(){
            th = new Thread(this);
            th.start();
        }
        
        public void play(){
            if(oldFile == null | (oldFile != null && oldFile != file)){
                try {
                    prepareFirst();
                    prepareAudio();
                    prepareVideo();
                } catch (Exception ex) {
                    Logger.getLogger(AVEntry.class.getName()).log(Level.SEVERE, null, ex);
                }                    
            }
            playing = true;
            startSystemTime = System.currentTimeMillis();
            currentMediaTime = Time.toMillisecondsTime(start);            
        }
        
        public void pause(){
            playing = !playing;
        }
        
        public void stop(){
            playing = false;
        }
        
        public void setStartAt(Time start){
            this.start = start;
        }
        
        public void setStopAt(Time stop){
            this.stop = stop;
        }

        public PlayType getType() {
            return type;
        }

        public void setType(PlayType type) {
            this.type = type;
        }
        
        // <editor-fold defaultstate="collapsed" desc="Prepare First">
        
        private void prepareFirst() throws Exception{
            oldFile = file;
            
            // Initialize packet and check for error
            pPacket = av_packet_alloc();
            if(pPacket == null){
                throw new Exception("ALL: Couldn't allocate packet");
            }

            // Open video file
            if (avformat_open_input(pFormatCtx, file.getPath(), null, null) != 0) {
                throw new Exception("ALL: Couldn't open file");
            }

            // Retrieve stream information
            if (avformat_find_stream_info(pFormatCtx, (PointerPointer)null) < 0) {
                throw new Exception("ALL: Couldn't find stream information");
            }

            // Dump information about file onto standard error
            av_dump_format(pFormatCtx, 0, file.getPath(), 0);

            // Find the first audio/video stream
            for (int i = 0; i < pFormatCtx.nb_streams(); i++) {
                switch(pFormatCtx.streams(i).codecpar().codec_type()){
                    case AVMEDIA_TYPE_VIDEO -> videoStreams.add(new StreamInfo(i, pFormatCtx.streams(i)));
                    case AVMEDIA_TYPE_AUDIO -> audioStreams.add(new StreamInfo(i, pFormatCtx.streams(i)));
                }
            }
            
            if(videoStreams.isEmpty() && type != PlayType.AudioOnly){
                throw new Exception("Didn't find an audio stream");
            }
            if(audioStreams.isEmpty() && type != PlayType.VideoOnly){
                throw new Exception("Didn't find a video stream");
            }
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Prepare Audio">
        
        private void prepareAudio() throws Exception{
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // AUDIO
            //------------------------------------------------------------------

            if(audioStreams.isEmpty() == false){
                //===========================
                //------------
                
//                // Let's search for AVCodec
//                pAudioCodec = avcodec_find_decoder(pFormatCtx.streams(audioStreams.get(0).getStreamIndex()).codecpar().codec_id());
//                if (pAudioCodec == null) {
//                    throw new Exception("AUDIO: Unsupported codec or not found!");
//                }
//
//                // Let's alloc AVCodecContext
//                pAudioCodecCtx = avcodec_alloc_context3(pAudioCodec);
//                if (pAudioCodecCtx == null) {            
//                    throw new Exception("AUDIO: Unallocated codec context or not found!");
//                }
                
                // Get a pointer to the codec context for the video stream
                pAudioCodecCtx = pFormatCtx.streams(audioStreams.get(0).getStreamIndex()).codec();

                // Find the decoder for the video stream
                pAudioCodec = avcodec_find_decoder(pAudioCodecCtx.codec_id());
                if (pAudioCodec == null) {
                    throw new Exception("AUDIO: Unsupported codec or not found!");
                }

                //===========================
                //------------

                /* open it */
                if (avcodec_open2(pAudioCodecCtx, pAudioCodec, OPTIONS_DICT) < 0) {
                    throw new Exception("AUDIO: Could not open codec");
                }
                
                // Allocate audio frame
                pAudioFrame = av_frame_alloc();
                if (pAudioFrame == null){
                    throw new Exception("AUDIO: Frame allocation failed");
                }

                pAudioDecodedFrame = av_frame_alloc();
                if (pAudioDecodedFrame == null){
                    throw new Exception("AUDIO: DecodedFrame allocation failed");
                }

                audio_swr_ctx = swr_alloc_set_opts(
                        null,                           // existing Swr context or NULL
                        AV_CH_LAYOUT_STEREO,            // output channel layout (AV_CH_LAYOUT_*)
                        AV_SAMPLE_FMT_S16,              // output sample format (AV_SAMPLE_FMT_*).
                        44100,                          // output sample rate (frequency in Hz)
                        pAudioCodecCtx.channels(),	// input channel layout (AV_CH_LAYOUT_*)
                        pAudioCodecCtx.sample_fmt(),	// input sample format (AV_SAMPLE_FMT_*).
                        pAudioCodecCtx.sample_rate(),	// input sample rate (frequency in Hz)
                        0,                              // logging level offset
                        null                            // parent logging context, can be NULL
                );
                
                swr_init(audio_swr_ctx);
                
                audio_data_size = av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);

                if (audio_data_size < 0) {
                    /* This should not occur, checking just for paranoia */
                    throw new Exception("Failed to calculate data size");
                }
                
                audio_data = new BytePointer(av_malloc(audio_data_size));
                
                av_samples_fill_arrays(
                        pAudioDecodedFrame.data(),      // audio_data,
                        pAudioDecodedFrame.linesize(),  // linesize
                        audio_data,                     // buf
                        (int)AV_CH_LAYOUT_STEREO,       // nb_channels
                        44100,                          // nb_samples
                        AV_SAMPLE_FMT_S16,              // sample_fmt
                        0                               // align
                );
                
            }
            
            // Audio treatment end ---------------------------------------------
            //==================================================================
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Prepare Video">
        
        private void prepareVideo() throws Exception{
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // VIDEO
            //------------------------------------------------------------------

            if(videoStreams.isEmpty() == false){
                
                /* New way to do (still doesn't work) : */
                
//                // Let's search for AVCodec
//                pVideoCodec = avcodec_find_decoder(pFormatCtx.streams(videoStreams.get(0).getStreamIndex()).codecpar().codec_id());
//                if (pVideoCodec == null) {
//                    throw new Exception("VIDEO: Unsupported codec or not found!");
//                }
//
//                // Let's alloc AVCodecContext
//                pVideoCodecCtx = avcodec_alloc_context3(pVideoCodec);
//                if (pVideoCodecCtx == null) {            
//                    throw new Exception("VIDEO: Unallocated codec context or not found!");
//                }
                /* New way - end */
                
                /* Old way to do : */
                
                // Get a pointer to the codec context for the video stream
                pVideoCodecCtx = pFormatCtx.streams(videoStreams.get(0).getStreamIndex()).codec();

                // Find the decoder for the video stream
                pVideoCodec = avcodec_find_decoder(pVideoCodecCtx.codec_id());
                if (pVideoCodec == null) {
                    throw new Exception("VIDEO: Unsupported codec or not found!");
                }
                
                /* Old way - end */

                // Open codec
                if (avcodec_open2(pVideoCodecCtx, pVideoCodec, OPTIONS_DICT) < 0) {
                    throw new Exception("VIDEO: Could not open codec");
                }

                // Allocate video frame
                pVideoFrame = av_frame_alloc();

                // Allocate an AVFrame structure
                pVideoFrameRGB = av_frame_alloc();
                if(pVideoFrameRGB == null) {
                    throw new Exception("VIDEO: Could not allocate frame");
                }

                int width = pFormatCtx.streams(videoStreams.get(0).getStreamIndex()).codecpar().width();
                int height = pFormatCtx.streams(videoStreams.get(0).getStreamIndex()).codecpar().height();

                /*
                Return the size in bytes of the amount of data required
                to store an image with the given parameters. 
                */
                video_numBytes = av_image_get_buffer_size(
                        AV_PIX_FMT_BGR24,           // pixel format
                        width,                      // width
                        height,                     // height
                        1                           // the assumed linesize alignment
                );

                video_buffer = new BytePointer(av_malloc(video_numBytes));
                
                /*
                Allocate and return an SwsContext.
                You need it to perform scaling/conversion operations using sws_scale().
                */
                video_sws_ctx = sws_getContext(
                        width,                      // the width of the source image
                        height,                     // the height of the source image
                        pVideoCodecCtx.pix_fmt(),   // the source image format
                        width,                      // the width of the destination image
                        height,                     // the height of the destination image
                        AV_PIX_FMT_BGR24,           // the destination image format
                        SWS_BILINEAR,               // specify which algorithm and options to use for rescaling
                        null,                       // extra parameters to tune the used scaler
                        null,                       // extra parameters to tune the used scaler
                        (DoublePointer)null         // extra parameters to tune the used scaler
                );
                
                // Fill array
                av_image_fill_arrays(
                        pVideoFrameRGB.data(),
                        pVideoFrameRGB.linesize(),
                        video_buffer,
                        AV_PIX_FMT_BGR24,
                        width,
                        height,
                        1
                );
            }            

            // Video treatment end ---------------------------------------------
            //==================================================================
        }
        
        // </editor-fold>
        
        private void doPlay() throws Exception{
            av_init_packet(pPacket);
            
            // Set time
            if(Time.isSame(start, stop) == false){
                AVRational source = null;
                                
                if(audioStreams.isEmpty() == false) source = audioStreams.get(0).getInfo().time_base();
                if(videoStreams.isEmpty() == false) source = videoStreams.get(0).getInfo().time_base();
                
                if(source != null){                    
                    double fps = (double)source.num() / (double)source.den();
                    int frame = Time.getFrame(start, fps);
                    pFormatCtx.seek2any(frame);
                }
            }
                    

            // Read frames
            while (playing && av_read_frame(pFormatCtx, pPacket) >= 0) {
                if (type != PlayType.AudioOnly && pPacket.stream_index() == videoStreams.get(0).getStreamIndex()) {
                    // Is this a packet from the video stream?
                    decodeVideo();
                    renewPacket();
                }

                if (type != PlayType.VideoOnly && pPacket.stream_index() == audioStreams.get(0).getStreamIndex()) {
                    // Is this a packet from the audio stream?
                    decodeAudio();
                    renewPacket();
                }
                
            }
        }
        
        private void renewPacket(){
            // Free the packet that was allocated by av_read_frame
            av_packet_unref(pPacket);

            pPacket.data(null);
            pPacket.size(0);
            av_init_packet(pPacket);
        }
        
        // <editor-fold defaultstate="collapsed" desc="Decode Audio">
        
        private void decodeAudio() throws Exception{
            
            BytePointer[] samples_ptr = new BytePointer[] { null };
            Buffer[] samples_buf = new Buffer[] { null };
        
            System.out.println("< 01 >");
            do {
                audio_ret = avcodec_send_packet(pAudioCodecCtx, pPacket);
            } while(audio_ret == AVERROR_EAGAIN());
            System.out.println("packet sent return value: " + audio_ret);

            if(audio_ret == AVERROR_EOF || audio_ret == AVERROR_EINVAL()) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("AVERROR(EAGAIN): %d, AVERROR_EOF: %d, AVERROR(EINVAL): %d\n", AVERROR_EAGAIN(), AVERROR_EOF, AVERROR_EINVAL());
                formatter.format("Audio frame getting error (%d)!\n", audio_ret);
                throw new Exception(sb.toString());
            }

            audio_ret = avcodec_receive_frame(pAudioCodecCtx, pAudioFrame);
            System.out.println("frame received return value: " + audio_ret);
            
            int audio_buffer_size = swr_convert(
                    audio_swr_ctx,              // allocated Swr context, with parameters set 
                    pAudioDecodedFrame.data(),  // output buffers
                    44100,                      // amount of space available for output in samples per channel 
                    pAudioFrame.data(),         // input buffers
                    pAudioFrame.nb_samples()    // number of input samples available in one channel
            );
            
            int sample_format = pAudioDecodedFrame.format();
            int planes = av_sample_fmt_is_planar(sample_format) != 0 ? (int)pAudioDecodedFrame.channels() : 1;
            
            int data_size = av_samples_get_buffer_size(
                    (IntPointer)null,               // dst linesize
                    (int)AV_CH_LAYOUT_STEREO,       // dst nb_channels
                    audio_buffer_size,              // swr_convert_ret
                    AV_SAMPLE_FMT_S16,              // dst sample fmt
                    1) / planes;                    // 
            
            if (samples_buf == null || samples_buf.length != planes) {
                samples_ptr = new BytePointer[planes];
                samples_buf = new Buffer[planes];
            }
            
            int sample_size = data_size / av_get_bytes_per_sample(sample_format);
            
            for (int i = 0; i < planes; i++) {
                BytePointer p = pAudioDecodedFrame.data(i);
                if (!p.equals(samples_ptr[i]) || samples_ptr[i].capacity() < data_size) {
                    samples_ptr[i] = p.capacity(data_size);
                    ByteBuffer b   = p.asBuffer();
                    samples_buf[i] = b.asShortBuffer(); // S16, S16P
                }
                samples_buf[i].position(0).limit(sample_size);
            }
            
            AudioFormat audioFormat = new AudioFormat(
                    pAudioDecodedFrame.sample_rate(), 
                    16, 
                    pAudioDecodedFrame.channels(), 
                    true, 
                    true);

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            soundLine.start();

            ExecutorService executor = Executors.newSingleThreadExecutor();

            while (!Thread.interrupted()) { 
                
                    ShortBuffer channelSamplesFloatBuffer = (ShortBuffer) samples_buf[0];
                    channelSamplesFloatBuffer.rewind();

                    ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesFloatBuffer.capacity() * 2);

                    for (int i = 0; i < channelSamplesFloatBuffer.capacity(); i++) {
                        short val = channelSamplesFloatBuffer.get(i);
                        outBuffer.putShort(val);
                    }

                    /**
                     * We need this because soundLine.write ignores
                     * interruptions during writing.
                     */
                    try {
                        executor.submit(() -> {
                            soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                            outBuffer.clear();
                        }).get();
                    } catch (InterruptedException ex) {
                        th.interrupt();
                    } catch (ExecutionException ex) {
                        Logger.getLogger(AVEntry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
            }
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
            soundLine.stop();
            
//            double frame_nb = 44100d / pAudioCodecCtx.sample_rate() * pAudioFrame.nb_samples();
//            long out_count = Math.round(Math.floor(frame_nb));

//            int audio_buffer_size = swr_convert(
//                    audio_swr_ctx,              // allocated Swr context, with parameters set 
//                    pAudioDecodedFrame.data(),  // output buffers
//                    44100,                      // amount of space available for output in samples per channel 
//                    pAudioFrame.data(),         // input buffers
//                    pAudioFrame.nb_samples()    // number of input samples available in one channel
//            );
//            
//            System.out.println("Original = " + pAudioFrame.isNull());
//            System.out.println("Decoded = " + pAudioDecodedFrame.isNull());
//            
//            System.out.println("< 02 >");
//            
//            if(audio_buffer_size > 0){
//                System.out.println("< 03 >");
//                
//                int dst_bufsize = av_samples_get_buffer_size(
//                        pAudioDecodedFrame.linesize(),  // dst linesize
//                        (int)AV_CH_LAYOUT_STEREO,       // dst nb_channels
//                        audio_buffer_size,              // swr_convert_ret
//                        AV_SAMPLE_FMT_S16,              // dst sample fmt
//                        1                               // 
//                );
//                
//                System.out.println("< 04 >");
//                
//                AudioFormat audioFormat = new AudioFormat(
//                        44100,      // 
//                        16,         // 
//                        2,          // 
//                        true,       // 
//                        false       // 
//                );
//                
//                System.out.println("< 05 >");
//                System.out.println("DATA is " + (pAudioDecodedFrame.data()));
//                
//                for(int channel = 0; channel < pAudioDecodedFrame.channels(); channel++){
//                    
//                    System.out.println("< 06 ::inloop:: " + channel + " >");
//                    
//                    BytePointer bytePointer = pAudioDecodedFrame.data(channel);
//                    
//                    System.out.println("< 07 ::inloop:: " + channel + " >");
//
//                    // Fill bytes
//                    byte[] bytes =  bytePointer.asBuffer().array();
//                    
//                    System.out.println("< 08 ::inloop:: " + channel + " >");
//
//                    try (SourceDataLine sdl = AudioSystem.getSourceDataLine(audioFormat)) {
//                        System.out.println("< 09 ::inAUDIO:: >");
//                        
//                        sdl.open(audioFormat); 
//                        
//                        System.out.println("< 10 ::inAUDIO:: >");
//                        
//                        sdl.start();
//                        
//                        System.out.println("< 11 ::inAUDIO:: >");
//                        
//                        int remain = bytes.length;
//                        int length;
//                        while(remain > 0){
//                            
//                            System.out.println("< 12 ::inAUDIO::inloop:: >");
//                            
//                            length = remain < dst_bufsize ? remain : dst_bufsize;
//                            sdl.write(bytes, 0, length);
//                            
//                            System.out.println("< 13 ::inAUDIO::inloop:: >");
//                            remain -= length;
//                        }
//                        
//                        System.out.println("< 14 ::inAUDIO:: >");
//                        
//                        sdl.drain();
//                        
//                        System.out.println("< 15 ::inAUDIO:: >");
//                        
//                        sdl.stop();
//                        
//                        System.out.println("< 16 ::inAUDIO:: >");
//                        
//                    } catch (LineUnavailableException ex) {
//                        Logger.getLogger(AVEntry.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
            
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Decode Video">

        private void decodeVideo() throws Exception{
            // Decoding step 1
            int ret = avcodec_send_packet(pVideoCodecCtx, pPacket);

            // Define exception for packet error if ret is negative
            if (ret < 0) {
                throw new Exception("Error sending a packet for decoding");
            }

            // While ret is positive then let's loop
            while (ret >= 0) {
                // Decoding step 2
                ret = avcodec_receive_frame(pVideoCodecCtx, pVideoFrame);

                // If there is no data anymore then returns otherwise if ret is negative triggers an exception
                if (ret == AVERROR_EAGAIN() || ret == AVERROR_EOF){
                    return;
                } else if (ret < 0) {
                    throw new Exception("Error during decoding");
                }

                // Convert the image from its native format to RGB
                sws_scale(
                        video_sws_ctx, 
                        pVideoFrame.data(), 
                        pVideoFrame.linesize(), 
                        0, 
                        pVideoCodecCtx.height(), 
                        pVideoFrameRGB.data(), 
                        pVideoFrameRGB.linesize()
                ); 


                //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
                // Display the (lonely) frame
                //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
                
                long timeMsCounter = 0L;
                
                int num = pVideoCodecCtx.time_base().num();
                int den = pVideoCodecCtx.time_base().den();
                float fps = (float)num / (float)den;
                double delay = pPacket.pts() * fps;
                double extra_delay = pVideoFrameRGB.repeat_pict() / 2 * fps;
                
                delay += extra_delay;
                
                while(timeMsCounter < (delay / 1000d)){
                    // We waiting for a good time
                    try{
                        java.util.concurrent.TimeUnit.MILLISECONDS.sleep(1);
                    }catch(InterruptedException exc){
                        // Nothing but quit
                        break;
                    }                    
                    timeMsCounter++;
                    fireVideoTimeElapsed(getMediaElapsedDuration());
                }

                if(Time.toMillisecondsTime(stop) != 0L && currentMediaTime / 1000 >= Time.toMillisecondsTime(stop)){
                    stop();
                    fireStopInvoked();
                }

                // Convert to system Java image from PixelFormat and AVFrame to BufferdImage
                BufferedImage img = GetImage(pVideoFrameRGB, pVideoCodecCtx.width(), pVideoCodecCtx.height());
                ImageEvent imageEvent = new ImageEvent(img);
                fireImageChanged(imageEvent);

                //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

            }
        }
        
        // </editor-fold>
        
        private double getMediaElapsedDuration(){
            // We calculate the current time of the media
            long systemTime = System.currentTimeMillis();
            currentMediaTime = currentMediaTime + (systemTime - startSystemTime);
            System.out.println("M: "+(currentMediaTime / 1000d));
            return currentMediaTime / 1000d;
        }
        
        // <editor-fold defaultstate="collapsed" desc="Free resources">    
        private void freeAudio() {
            swr_free(audio_swr_ctx);
            avcodec_free_context(pAudioCodecCtx);
            av_parser_close(pAudioParser);
            av_free(audio_data);
            av_frame_free(pAudioDecodedFrame);
            av_frame_free(pAudioFrame);
            av_free(pAudioCodec);
            avcodec_close(pAudioCodecCtx);            
        }

        private void freeVideo(){
            avcodec_free_context(pVideoCodecCtx);
            // Free the RGB image
            av_free(video_buffer);
            av_frame_free(pVideoFrameRGB);
            // Free the YUV frame
            av_frame_free(pVideoFrame);
            // ?
            av_free(pVideoCodec);
            // Close the codec            
            avcodec_close(pVideoCodecCtx);
        }

        private void freeAudioVideo() {
            // Free the packet that was allocated by av_read_frame
            av_packet_unref(pPacket);
            // Close the video file
            avformat_close_input(pFormatCtx);
        }

        public void freeAllResources(){
            freeAudio();
            freeVideo();
            freeAudioVideo();
        }
        // </editor-fold>
        
        public void realloc(){
            //==================================================================
            // FFMpeg 4.0 - Video and Audio
            //==================================================================
            
            pFormatCtx = new AVFormatContext(null);
            OPTIONS_DICT = null;
            pPacket = new AVPacket();
            
            //==================================================================
            // FFMpeg 4.0 - Audio
            //==================================================================
            
            audioStreams = new ArrayList<>();
            audio_data = null;
            pAudioFrame = null;
            pAudioDecodedFrame = null;
            audio_swr_ctx = null;
    
            //==================================================================
            // FFMpeg 4.0 - Video
            //==================================================================
            
            videoStreams = new ArrayList<>();
            pVideoCodecCtx = null;
            pVideoCodec = null;
            pVideoFrame = null;
            pVideoFrameRGB = null;
            video_buffer = null;
            video_sws_ctx = null;
        }
    
        // <editor-fold defaultstate="collapsed" desc="BGR24 to ARGB">
        private BufferedImage GetImage(AVFrame pFrame, int width, int height){
            BufferedImage img_3BYTE_BGR;
            BufferedImage img_INT_ARGB = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Create output stream
            try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
                // Write pixel data
                BytePointer data = pFrame.data(0);
                byte[] bytes = new byte[width * 3];
                int l = pFrame.linesize(0);
                for(int y = 0; y < height; y++) {
                    data.position(y * l).get(bytes);                
                    baos.write(bytes);
                }

                // Create image
                img_3BYTE_BGR = createImage(ByteBuffer.wrap(baos.toByteArray()), width, height, BufferedImage.TYPE_3BYTE_BGR);

                // Convert to INT ARGB
                Graphics2D g2d = img_INT_ARGB.createGraphics();
                g2d.drawImage(img_3BYTE_BGR, 0, 0, null);
                g2d.dispose();
            } catch (IOException ex) {
                Logger.getLogger(AVEntry.class.getName()).log(Level.SEVERE, null, ex);
            }

            return img_INT_ARGB;
        }

        // From JavaAV (just a few)
        public BufferedImage createImage(ByteBuffer data, int width, int height, int type) {
                BufferedImage image = new BufferedImage(width, height, type);

                SampleModel model = image.getSampleModel();
                Raster raster = image.getRaster();
                DataBuffer outBuffer = raster.getDataBuffer();

                int x = -raster.getSampleModelTranslateX();
                int y = -raster.getSampleModelTranslateY();
                int step = model.getWidth() * model.getNumBands();
                int channels = model.getNumBands();

                data.position(0).limit(height * width * channels);

                if (model instanceof ComponentSampleModel compModel) {
                    step = compModel.getScanlineStride();
                    channels = compModel.getPixelStride();
                } else if (model instanceof SinglePixelPackedSampleModel singleModel) {
                    step = singleModel.getScanlineStride();
                    channels = 1;
                } else if (model instanceof MultiPixelPackedSampleModel multiModel) {
                    step = multiModel.getScanlineStride();
                    channels = multiModel.getPixelBitStride() / 8;
                }

                int start2 = y * step + x * channels;

                if (outBuffer instanceof DataBufferByte dataBufferByte) {
                    byte[] a = dataBufferByte.getData();
                    copy(data, step, ByteBuffer.wrap(a, start2, a.length - start2), step, false);
                } else if (outBuffer instanceof DataBufferShort dataBufferShort) {
                    short[] a = dataBufferShort.getData();
                    copy(data.asShortBuffer(), step / 2, ShortBuffer.wrap(a, start2, a.length - start2), step, true);
                } else if (outBuffer instanceof DataBufferUShort dataBufferUShort) {
                    short[] a = dataBufferUShort.getData();
                    copy(data.asShortBuffer(), step / 2, ShortBuffer.wrap(a, start2, a.length - start2), step, false);
                } else if (outBuffer instanceof DataBufferInt dataBufferInt) {
                    int[] a = dataBufferInt.getData();
                    copy(data.asIntBuffer(), step / 4, IntBuffer.wrap(a, start2, a.length - start2), step);
                } else if (outBuffer instanceof DataBufferFloat dataBufferFloat) {
                    float[] a = dataBufferFloat.getData();
                    copy(data.asFloatBuffer(), step / 4, FloatBuffer.wrap(a, start2, a.length - start2), step);
                } else if (outBuffer instanceof DataBufferDouble dataBufferDouble) {
                    double[] a = dataBufferDouble.getData();
                    copy(data.asDoubleBuffer(), step / 8, DoubleBuffer.wrap(a, start2, a.length - start2), step);
                }

                return image;
            }

            public void copy(ByteBuffer srcBuf, int srcStep, ByteBuffer dstBuf, int dstStep, boolean signed) {
                int w = Math.min(srcStep, dstStep);
                int srcLine = srcBuf.position();
                int dstLine = dstBuf.position();

                while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                    srcBuf.position(srcLine);
                    dstBuf.position(dstLine);

                    w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());

                    for (int x = 0; x < w; x++) {
                        int in = signed ? srcBuf.get() : srcBuf.get() & 0xFF;
                        byte out = (byte) in;
                        dstBuf.put(out);
                    }

                    srcLine += srcStep;
                    dstLine += dstStep;
                }
            }

            public void copy(ShortBuffer srcBuf, int srcStep, ShortBuffer dstBuf, int dstStep, boolean signed) {
                int w = Math.min(srcStep, dstStep);
                int srcLine = srcBuf.position();
                int dstLine = dstBuf.position();

                while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                    srcBuf.position(srcLine);
                    dstBuf.position(dstLine);

                    w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());

                    for (int x = 0; x < w; x++) {
                        int in = signed ? srcBuf.get() : srcBuf.get() & 0xFFFF;
                        short out = (short) in;
                        dstBuf.put(out);
                    }

                    srcLine += srcStep;
                    dstLine += dstStep;
                }
            }

            public void copy(IntBuffer srcBuf, int srcStep, IntBuffer dstBuf, int dstStep) {
                int w = Math.min(srcStep, dstStep);
                int srcLine = srcBuf.position();
                int dstLine = dstBuf.position();

                while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                    srcBuf.position(srcLine);
                    dstBuf.position(dstLine);

                    w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());

                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get();
                        int out = in;
                        dstBuf.put(out);
                    }

                    srcLine += srcStep;
                    dstLine += dstStep;
                }
            }

            public void copy(FloatBuffer srcBuf, int srcStep, FloatBuffer dstBuf, int dstStep) {
                int w = Math.min(srcStep, dstStep);
                int srcLine = srcBuf.position();
                int dstLine = dstBuf.position();

                while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                    srcBuf.position(srcLine);
                    dstBuf.position(dstLine);

                    w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());

                    for (int x = 0; x < w; x++) {
                        float in = srcBuf.get();
                        float out = in;

                        dstBuf.put(out);
                    }

                    srcLine += srcStep;
                    dstLine += dstStep;
                }
            }

            public void copy(DoubleBuffer srcBuf, int srcStep, DoubleBuffer dstBuf, int dstStep) {
                int w = Math.min(srcStep, dstStep);
                int srcLine = srcBuf.position();
                int dstLine = dstBuf.position();

                while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                    srcBuf.position(srcLine);
                    dstBuf.position(dstLine);

                    w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());

                    for (int x = 0; x < w; x++) {
                        double in = srcBuf.get();
                        double out = in;

                        dstBuf.put(out);
                    }

                    srcLine += srcStep;
                    dstLine += dstStep;
                }
            }            
            // </editor-fold>

        @Override
        public void run() {
            while(true){
                if(playing){
                    try {
                        doPlay();
                    } catch (Exception ex) {
                        Logger.getLogger(AVEntry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
    }
    
    // <editor-fold defaultstate="collapsed" desc="Events">
    //==========================================================================
    // Events
    //==========================================================================

    private final EventListenerList listeners = new EventListenerList();

    public void addAudioVideoListener(IAudioVideoListener listener) {
        listeners.add(AudioVideoListener.class, (AudioVideoListener)listener);
    }

    public void removeAudioVideoListener(IAudioVideoListener listener) {
        listeners.remove(AudioVideoListener.class, (AudioVideoListener)listener);
    }

    public Object[] getListeners() {
        return listeners.getListenerList();
    }

    protected void fireEndOfAudio() {
        for(Object o : getListeners()){
            if(o instanceof AudioVideoListener listen){
                listen.audioEnded();
                break;
            }
        }
    }

    protected void fireStopInvoked() {
        for(Object o : getListeners()){
            if(o instanceof AudioVideoListener listen){
                listen.audioStopReached();
                break;
            }
        }
    }

    protected void fireImageChanged(ImageEvent event) {
        for(Object o : getListeners()){
            if(o instanceof AudioVideoListener listen){
                listen.videoImageChanged(event);
                break;
            }
        }
    }

    protected void fireEndOfVideo() {
        for(Object o : getListeners()){
            if(o instanceof AudioVideoListener listen){
                listen.videoEnded();
                break;
            }
        }
    }

    protected void fireVideoTimeElapsed(double ms) {
        for(Object o : getListeners()){
            if(o instanceof AudioVideoListener listen){
                listen.videoTimeElapsed(ms);
                break;
            }
        }
    }
    // </editor-fold>
}
