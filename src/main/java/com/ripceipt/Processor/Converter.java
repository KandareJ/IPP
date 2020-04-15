package com.ripceipt.Processor;

import org.ghost4j.document.PSDocument;
import org.ghost4j.renderer.SimpleRenderer;

import javax.imageio.ImageIO;
import java.io.*;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.util.List;

public class Converter {
    public Converter() {

    }

    public byte[] convert(byte[] document) {
        try {
            PSDocument doc = new PSDocument();
            doc.load(new ByteArrayInputStream(document));
            SimpleRenderer renderer = new SimpleRenderer();
            renderer.setResolution(50);
            List<Image> images = renderer.render(doc);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            ImageIO.write((RenderedImage) images.get(0), "png", os);
            //ImageIO.write((RenderedImage) images.get(0), "png", new File("final.png"));

            System.out.println("Size: " + os.size());

            return os.toByteArray();


            //Decoder decoder = new Decoder(os.toByteArray());
            //decoder.decode();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
