
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Downloader {
    private static String pathToDirectory = "D:\\graphs";

    public static ImageIcon downloadImage(String strImageURL) {
        try {
            System.out.println(strImageURL);
            URL urlImage = new URL(strImageURL);
            if (strImageURL.contains(".svg")) {
                Path path = Paths.get(pathToDirectory);
                if (!Files.exists(path)) {
                    new File(pathToDirectory).mkdirs();
                    System.out.println("Directory has been created!");
                }
                File svgImage = new File(pathToDirectory + strImageURL.substring(strImageURL.lastIndexOf('/')));
                if (!svgImage.exists())
                    svgImage.createNewFile();
                try (BufferedInputStream in = new BufferedInputStream(urlImage.openStream()); FileOutputStream fileOutputStream = new FileOutputStream(svgImage)) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    return new ImageIcon(rasterize(svgImage).getScaledInstance(2 * Display.GRAPHIC_WIDTH, Display.GRAPHIC_HIGH, Image.SCALE_SMOOTH));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else return new ImageIcon(ImageIO.read(urlImage));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static BufferedImage rasterize(File svgFile) throws IOException {

        final BufferedImage[] imagePointer = new BufferedImage[1];
        String css = "svg {" +
                "shape-rendering: geometricPrecision;" +
                "text-rendering:  geometricPrecision;" +
                "color-rendering: optimizeQuality;" +
                "image-rendering: optimizeQuality;" +
                "}";
        File cssFile = File.createTempFile("batik-default-override-", ".css");
        FileUtils.writeStringToFile(cssFile, css);

        TranscodingHints transcoderHints = new TranscodingHints();
        transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
        transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION,
                SVGDOMImplementation.getDOMImplementation());
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
                SVGConstants.SVG_NAMESPACE_URI);
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
        transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString());

        try {

            TranscoderInput input = new TranscoderInput(new FileInputStream(svgFile));

            ImageTranscoder t = new ImageTranscoder() {

                @Override
                public BufferedImage createImage(int w, int h) {
                    return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                }

                @Override
                public void writeImage(BufferedImage image, TranscoderOutput out)
                        throws TranscoderException {
                    imagePointer[0] = image;
                }
            };
            t.setTranscodingHints(transcoderHints);
            t.transcode(input, null);
        } catch (TranscoderException ex) {
            // Requires Java 6
            ex.printStackTrace();
            throw new IOException("Couldn't convert " + svgFile);
        } finally {
            cssFile.delete();
        }

        return imagePointer[0];
    }
}