package com.neovisionaries.ws.client;

import java.io.IOException;

/**
 * User: leoye
 * Date: 6/28/20
 * Time: 9:45 AM
 * ShareAct Inc. All rights reserved.
 */
public class WritingDirect extends WritingThread {
    private long lastFlushAt = System.currentTimeMillis();

    public WritingDirect(WebSocket websocket) {
        super(websocket);
    }

    @Override
    public void runMain() {
    }

    @Override
    public void requestStop() {
        notifyFinished();
    }

    @Override
    public synchronized boolean queueFrame(WebSocketFrame frame) {
        try {
            // Send the frame to the server.
            sendFrame(frame);

            // If the frame is PING or PONG.
            if (frame.isPingFrame() || frame.isPongFrame() || isFlushNeeded(false)) {
                // Deliver the frame to the server immediately.
                doFlush();
                lastFlushAt = System.currentTimeMillis();
            } else {
                lastFlushAt = flushIfLongInterval(lastFlushAt);
            }
            return true;
        } catch (WebSocketException e) {
            // ignore
            return false;
        }
    }

    @Override
    public synchronized void queueFlush() {
        try {
            doFlush();
        } catch (WebSocketException e) {
            //ignore
        }
    }

    @Override
    public void run() {
    }

    @Override
    public void callOnThreadCreated() {
        mWebSocket.onWritingThreadStarted();
    }

    @Override
    public synchronized void start() {
        throw new IllegalArgumentException("No need to start thread");
    }
}
