/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2003, 2016 Robert Withers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ******************************************************************************
 * murmur/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the Squeak/Pharo communities and the cryptography 
 * team, which are this software's foundation.
 *******************************************************************************/

package club.callistohouse.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class NetworkUtil {

    public static final String LOOPBACK_ADDRESS = "127.0.0.1";

    /**
     * Convert an InetAddress into a long
     * 
     * @return - The IP address as a long
     */
    public static long cvtInetAddressToLong(InetAddress addr) {

        long ipAsLong = 0;

        if (addr != null) {

            // Convert the IP address to a kibf
            byte[] b = addr.getAddress();
            for (int i = 0; i < b.length; i++) {
                ipAsLong = (ipAsLong << 8) | (int) (b[i] & 0xFF);
            }
        }

        return ipAsLong;
    }

    /**
     * Find a free port
     * 
     * @return - a free port
     * @throws IOException
     */
    public static int findFreePort() {

        int port = 0;

        try {

            ServerSocket server = new ServerSocket(0);
            port = server.getLocalPort();
            server.close();
            return port;
        } catch (IOException ioe) {

            System.out.println("Could not find free port.\n" + ioe.getMessage());
        }

        return port;
    }
}
