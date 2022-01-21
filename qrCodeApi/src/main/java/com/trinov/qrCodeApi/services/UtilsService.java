package com.trinov.qrCodeApi.services;

import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.trinov.qrCodeApi.enums.Colors;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

@Service
public class UtilsService {

    public int getColor(String color) {
        switch (color.toUpperCase()) {
            case "WHITE":
                return Colors.WHITE.getArgb();
            case "YELLOW":
                return Colors.YELLOW.getArgb();
            case "ORANGE":
                return Colors.ORANGE.getArgb();
            case "BLUE":
                return Colors.BLUE.getArgb();
            default:
                return Colors.BLACK.getArgb();
        }
    }

    public Mono<InputStream> getInputStream(FilePart filePart) {
        return filePart.content()
                .map(dataBuffer -> dataBuffer.asInputStream(true))
                .collectList()
                .map(inputStreams -> new SequenceInputStream(Collections.enumeration(inputStreams)));
    }

    public String combineQRCodes(ArrayList<String> qrCodes) {
        return qrCodes.stream()
                .map(m -> {
                    String[] data = m.split("@");
                    if (data.length < 2) {
                        return new Object[]{-1, ""};
                    } else {
                        try {
                            int sortIndex = Integer.parseInt(data[0]);
                            return new Object[]{sortIndex, data[1]};
                        } catch (NumberFormatException e) {
                            return new Object[]{-1, ""};
                        }
                    }
                })
                .filter(data -> (int) data[0] >= 0)
                .sorted(Comparator.comparingInt(data -> (int) data[0]))
                .map(d -> (String) d[1])
                .reduce("", (acc, s) -> acc + s);
    }

    public void iterateVideoFrames(InputStream inputStream, Consumer<byte[]> consumer) throws IOException {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream);
        int count = 0;
        frameGrabber.start();
        System.out.println("[Frame Rate] : " + frameGrabber.getFrameRate());
        while (true) {
            Java2DFrameConverter c = new Java2DFrameConverter();
            Frame f = frameGrabber.grab();
            if (f == null) {
                frameGrabber.stop();
                System.out.println("DONE!!");
                break;
            }
            BufferedImage bi = c.convert(f);
            if (bi == null) {
                continue;
            }
            System.out.println("[Frame] : " + ++count);
            byte[] byteArray = this.bufferedImageToBytes(bi, "jpg");
            consumer.accept(byteArray);
        }
    }

    public BufferedImage bitMatrixToBufferedImage(BitMatrix qrCode, MatrixToImageConfig imageConfig) {
        return MatrixToImageWriter.toBufferedImage(qrCode, imageConfig);
    }

    public byte[] bitMatrixToBytes(BitMatrix qrCode, String format, MatrixToImageConfig imageConfig) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(qrCode, format, outputStream, imageConfig);
        byte[] QRCodeBytes = outputStream.toByteArray();
        outputStream.close();
        return QRCodeBytes;
    }

    public byte[] bufferedImageToBytes(BufferedImage qrCode, String format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrCode, format, outputStream);
        byte[] QRCodeBytes = outputStream.toByteArray();
        outputStream.close();
        return QRCodeBytes;
    }

    public String bitMatrixToBase64(BitMatrix qrCode, String format, MatrixToImageConfig imageConfig) throws IOException {
        return Base64.getEncoder().encodeToString(this.bitMatrixToBytes(qrCode, format, imageConfig));
    }

    public void saveBitMatrix(BitMatrix qrCode, String format, String filePath) throws IOException {
        MatrixToImageWriter.writeToPath(qrCode, format, Paths.get(filePath));
    }

}
