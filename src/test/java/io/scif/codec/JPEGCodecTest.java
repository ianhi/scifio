/*-
 * #%L
 * SCIFIO library for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2011 - 2026 SCIFIO developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package io.scif.codec;

import static org.junit.Assert.assertArrayEquals;

import io.scif.FormatException;
import io.scif.SCIFIO;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link JPEGCodec}.
 */
public class JPEGCodecTest {

	private static SCIFIO scifio;

	private static Codec codec;

	@BeforeClass
	public static void createContext() {
		scifio = new SCIFIO();
		codec = scifio.codec().getCodec(JPEGCodec.class);
	}

	@AfterClass
	public static void disposeContext() {
		scifio.getContext().dispose();
	}

	@Test
	public void testBytesBeforeStartMarker() throws FormatException,
		IOException
	{
		final byte[] ramp = new byte[64];
		for (int i = 0; i < ramp.length; i++) {
			ramp[i] = (byte) (i * 4);
		}
		final CodecOptions options = CodecOptions.getDefaultOptions();
		options.width = 8;
		options.height = 8;
		options.channels = 1;
		options.bitsPerSample = 8;
		final byte[] jpeg = codec.compress(ramp, options);

		final byte[] withJunk = new byte[5 + jpeg.length];
		System.arraycopy(jpeg, 0, withJunk, 5, jpeg.length);

		assertArrayEquals(codec.decompress(jpeg, options), codec.decompress(
			withJunk, options));
	}
}
