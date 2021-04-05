/** 
 * Extracted and modified from version in QuickTheories. Copyright
 * notice from that version included below. 
 *
 * Copyright 2010 Henry Coles
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.quicktheories.coverage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class Installer {

  protected static final String      CAN_REDEFINE_CLASSES  = "Can-Redefine-Classes";
  protected static final String      CAN_RETRANSFORM_CLASSES  = "Can-Retransform-Classes";
  protected static final String      PREMAIN_CLASS         = "Premain-Class";
  protected static final String      AGENT_CLASS         = "Agent-Class";  

  protected static final String      CAN_SET_NATIVE_METHOD = "Can-Set-Native-Method-Prefix";
  protected static final String      BOOT_CLASSPATH        = "Boot-Class-Path";

  private static final String        AGENT_CLASS_NAME      = CoverageAgent.class
      .getName();

  private final ClassloaderByteArraySource classByteSource;

  public Installer(final ClassloaderByteArraySource classByteSource) {
    this.classByteSource = classByteSource;
  }


  public File createJar() {
    try {

      final File randomName = File.createTempFile(randomFilename(),
          ".jar");
      final FileOutputStream fos = new FileOutputStream(randomName);
      createJarFromClassPathResources(fos, randomName.getAbsolutePath());
      fos.close();
      return randomName;

    } catch (final IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  static String randomFilename() {
      return System.currentTimeMillis()
              + ("" + Math.random()).replaceAll("\\.", "");
  }


    private void createJarFromClassPathResources(final FileOutputStream fos,
      final String location) throws IOException {
    final Manifest m = new Manifest();

    m.clear();
    final Attributes global = m.getMainAttributes();
    if (global.getValue(Attributes.Name.MANIFEST_VERSION) == null) {
      global.put(Attributes.Name.MANIFEST_VERSION, "1.0");
    }
    final File mylocation = new File(location);
    global.putValue(BOOT_CLASSPATH, getBootClassPath(mylocation));
    global.putValue(PREMAIN_CLASS, AGENT_CLASS_NAME);
    global.putValue(AGENT_CLASS, AGENT_CLASS_NAME);    
    global.putValue(CAN_REDEFINE_CLASSES, "true");
    global.putValue(CAN_RETRANSFORM_CLASSES, "true");
    global.putValue(CAN_SET_NATIVE_METHOD, "true");

    final JarOutputStream jos = new JarOutputStream(fos, m);
    addClass(CoverageAgent.class, jos);
    jos.close();
  }

  private String getBootClassPath(final File mylocation) {
    return mylocation.getAbsolutePath().replace('\\', '/');
  }

  private void addClass(final Class<?> clazz, final JarOutputStream jos)
      throws IOException {
    final String className = clazz.getName();
    final ZipEntry ze = new ZipEntry(className.replace(".", "/") + ".class");
    jos.putNextEntry(ze);
    jos.write(classBytes(className));
    jos.closeEntry();
  }

  private byte[] classBytes(final String className) {
    final Optional<byte[]> bytes = this.classByteSource.getBytes(className);

    if (bytes.isPresent()) {
      return bytes.get();
    }

    throw new RuntimeException("Unable to load class content for " + className);
  }

}
