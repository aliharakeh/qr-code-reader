package com.trinov.qrCodeApi.services;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Service
public class QRCodeService {

    @Autowired
    UtilsService utilsService;

    public BitMatrix generateQRCode(String data, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        return qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
    }

    public BufferedImage multiQRCodes(ArrayList<String> data, int width, int height, MatrixToImageConfig imageConfig, int countPerRow) throws WriterException {
        int totalHeight = height * (int) Math.ceil((double) data.size() / countPerRow);
        int totalWidth = width * countPerRow;
        BufferedImage finalImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) finalImage.getGraphics();
        int x = 0;
        int y = 0;
        int currentRowCount = 0;
        int currentRow = 0;
        for (String qrCodeText : data) {
            BitMatrix qrCode = this.generateQRCode(qrCodeText, width, height);
            graphics.drawImage(utilsService.bitMatrixToBufferedImage(qrCode, imageConfig), x, y, null);
            x = width * (currentRowCount + 1);
            currentRowCount++;
            if (currentRowCount == countPerRow) {
                currentRow++;
                currentRowCount = 0;
                x = 0;
            }
            y = height * currentRow;
        }
        graphics.dispose();
        return finalImage;
    }

    public BufferedImage addLogo(BufferedImage qrImage, String logoPath) throws IOException {
        BufferedImage logoImage = ImageIO.read(new File(logoPath));
        BufferedImage finalImage = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) finalImage.getGraphics();
        graphics.drawImage(qrImage, 0, 0, null);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        int centerY = (qrImage.getHeight() - logoImage.getHeight()) / 2;
        int centerX = (qrImage.getWidth() - logoImage.getWidth()) / 2;
        graphics.drawImage(logoImage, Math.round(centerX), Math.round(centerY), null);
        graphics.dispose();
        return finalImage;
    }

    public ArrayList<String> detectQRCodes(InputStream inputStream) throws IOException {
        BufferedImage input = ImageIO.read(inputStream);
//        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(input)));
//        Result[] qrCodeResult;
//        try {
//            qrCodeResult = new QRCodeMultiReader().decodeMultiple(binaryBitmap);
//            return (ArrayList<String>) Arrays
//                    .stream(qrCodeResult)
//                    .map(Result::getText)
//                    .collect(Collectors.toList());
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());

        GrayU8 gray = ConvertBufferedImage.convertFrom(input, (GrayU8) null);
        QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(null, GrayU8.class);
        detector.process(gray);
        java.util.List<QrCode> detections = detector.getDetections();
        ArrayList<String> codes = new ArrayList<>();
        for (QrCode qr : detections) {
            codes.add(qr.message);
        }
//            java.util.List<QrCode> fs = detector.getFailures();
//            System.out.println("Failures = " + fs.size());
//            fs.forEach(f -> System.out.println(f.failureCause));
        return codes;
//        }
    }

}
