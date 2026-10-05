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

import static org.junit.Assert.assertEquals;

import io.scif.FormatException;
import io.scif.SCIFIO;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link RPZACodec}.
 */
public class RPZACodecTest {

	private static SCIFIO scifio;

	private static Codec codec;

	@BeforeClass
	public static void createContext() {
		scifio = new SCIFIO();
		codec = scifio.codec().getCodec(RPZACodec.class);
	}

	@AfterClass
	public static void disposeContext() {
		scifio.getContext().dispose();
	}

	@Test(timeout = 5000)
	public void testDecodesFrame() throws FormatException {
		final byte[] frame = { 0, 0, 0, 0, 0, 0, 0, 0, // skipped by the codec
			(byte) 0xe1, 0, 0, 16, // chunk header: 0xe1 and a 24-bit length
			(byte) 0xa0, 0x7f, (byte) 0xff, // fill one block with colour 0x7fff
			0, 0, 0 };
		assertEquals(4 * 4 * 3, codec.decompress(frame, options()).length);
	}

	@Test(timeout = 5000, expected = FormatException.class)
	public void testMissingChunkHeader() throws FormatException {
		codec.decompress(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4 },
			options());
	}

	private static CodecOptions options() {
		final CodecOptions options = CodecOptions.getDefaultOptions();
		options.width = 4;
		options.height = 4;
		return options;
	}
}
