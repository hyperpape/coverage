/**
 * Extracted and modified from QuickTheories, originally copyright
 * Henry Coles
 */
package org.quicktheories.coverage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Optional;

public class ClassloaderByteArraySource  {

  private final ClassLoader loader;
  
  public ClassloaderByteArraySource(ClassLoader loader) {
    this.loader = loader;
  }

  public Optional<byte[]> getBytes(String clazz) {
    InputStream is = this.loader.getResourceAsStream(clazz.replace(".", "/") + ".class");
    if (is == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(StreamUtil.streamToByteArray(is));
    } catch(IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}

abstract class StreamUtil {

  public static byte[] streamToByteArray(final InputStream in)
      throws IOException {
    final ByteArrayOutputStream result = new ByteArrayOutputStream();
    copy(in, result);
    result.close();
    return result.toByteArray();
  }

  public static InputStream copyStream(final InputStream in) throws IOException {
    final byte[] bs = streamToByteArray(in);
    return new ByteArrayInputStream(bs);
  }

  private static void copy(final InputStream input, final OutputStream output)
      throws IOException {
    final ReadableByteChannel src = Channels.newChannel(input);
    final WritableByteChannel dest = Channels.newChannel(output);
    final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
    while (src.read(buffer) != -1) {
      buffer.flip();
      dest.write(buffer);
      buffer.compact();
    }
    buffer.flip();
    while (buffer.hasRemaining()) {
      dest.write(buffer);
    }
  }
}
