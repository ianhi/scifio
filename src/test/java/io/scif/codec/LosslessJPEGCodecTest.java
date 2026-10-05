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
 * Tests {@link LosslessJPEGCodec}.
 * <p>
 * Each file is the same 4x4 8-bit image, pixel(x, y) = 200 - 3x - 5y, encoded
 * by libjpeg-turbo with one of the predictors from ITU-T T.81 Table H.1. A
 * lossless decode must reproduce the image exactly.
 * </p>
 */
public class LosslessJPEGCodecTest {

	private static final String PREDICTOR_4 =
		"ffd8ffe000104a46494600010100000100010000ffc3000b080004000401011100ffc4" +
			"0017000101010100000000000000000000000000020307ffda0008010100040000e9" +
			"111190c8643fffd9";

	private static SCIFIO scifio;

	private static Codec codec;

	@BeforeClass
	public static void createContext() {
		scifio = new SCIFIO();
		codec = scifio.codec().getCodec(LosslessJPEGCodec.class);
	}

	@AfterClass
	public static void disposeContext() {
		scifio.getContext().dispose();
	}

	@Test
	public void testPredictor4() throws FormatException, IOException {
		assertArrayEquals(expected(), decode(PREDICTOR_4));
	}

	private static int[] expected() {
		final int[] pixels = new int[16];
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				pixels[y * 4 + x] = 200 - 3 * x - 5 * y;
			}
		}
		return pixels;
	}

	private static int[] decode(final String hex) throws FormatException,
		IOException
	{
		final byte[] bytes = codec.decompress(parseHex(hex), CodecOptions
			.getDefaultOptions());
		final int[] pixels = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			pixels[i] = bytes[i] & 0xff;
		}
		return pixels;
	}

	private static byte[] parseHex(final String hex) {
		final byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}
}
