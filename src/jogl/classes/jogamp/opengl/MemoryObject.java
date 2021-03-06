/**
 * Copyright 2010 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */

package jogamp.opengl;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.jogamp.common.util.HashUtil;

/**
 *
 */
public class MemoryObject {
    private long addr;
    private long size;
    private int  hash;
    private ByteBuffer buffer=null;

    public MemoryObject(long addr, long size) {
        this.addr = addr;
        this.size = size;
        this.hash = HashUtil.getAddrSizeHash32_EqualDist(addr, size);
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }

    /**
     * @return the 32bit hash value generated via {@link HashUtil#getAddrSizeHash32_EqualDist(long, long)}.
     */
    public int hashCode() {
        return hash;
    }

    public String toString() {
        return "MemoryObject[addr 0x"+Long.toHexString(addr)+", size 0x"+Long.toHexString(size)+", hash32: 0x"+Integer.toHexString(hash)+"]";
    }

    /**
     * @param map the identity HashMap, MemoryObject to MemoryObject
     * @param obj0 the MemoryObject
     * @return either the already mapped MemoryObject - not changing the map, or the newly mapped one.
     */
    public static MemoryObject getOrAddSafe(HashMap<MemoryObject,MemoryObject> map, MemoryObject obj0) {
        MemoryObject obj1 = map.get(obj0); // get identity (fast)
        if(null == obj1) {
            map.put(obj0, obj0);
            obj1 = obj0;
        }
        return obj1;
    }

}