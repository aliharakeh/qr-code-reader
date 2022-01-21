package com.trinov.qrCodeApi.controllers;

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.trinov.qrCodeApi.services.CompressionService;
import com.trinov.qrCodeApi.services.QRCodeService;
import com.trinov.qrCodeApi.services.UtilsService;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@CrossOrigin
public class QRController {

    @Autowired
    QRCodeService qrCodeService;

    @Autowired
    CompressionService compressionService;

    @Autowired
    UtilsService utilsService;

    @GetMapping(value = "/test", produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<ResponseEntity<byte[]>> generateQRCodeImage() {
        return Mono.create(sink -> {
            try {
                MatrixToImageConfig imageConfig = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);
                sink.success(ResponseEntity.ok(utilsService.bitMatrixToBytes(
                        qrCodeService.generateQRCode("QR Code Test", 200, 200),
                        "jpg",
                        imageConfig
                )));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        });
    }

    @PostMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<ResponseEntity<byte[]>> generateQRCodeImage(
            @RequestParam(defaultValue = "500") int width,
            @RequestParam(defaultValue = "500") int height,
            @RequestParam(defaultValue = "2953") int totalCharSize,
            @RequestParam(defaultValue = "3") int countPerRow,
            @RequestParam(defaultValue = "white") String qrBackgroundColor,
            @RequestParam(defaultValue = "black") String qrColor,
            @RequestBody() String qrCodeText
    ) {
        return Mono.create(monoSinkConsumer -> {
            MatrixToImageConfig imageConfig = new MatrixToImageConfig(utilsService.getColor(qrColor), utilsService.getColor(qrBackgroundColor));
            ArrayList<String> qrCodedata = new ArrayList<>();
            String qrCodeCompressedData = compressionService.lz4Compress(qrCodeText);
            int start = 0;
            int sortedIndex = 0;
            int compressionLength = qrCodeCompressedData.length();
            while (start < compressionLength) {
                String extraDetails = sortedIndex + "@";
                qrCodedata.add(extraDetails + qrCodeCompressedData.substring(start, Math.min(start + totalCharSize, compressionLength)));
                start += totalCharSize;
                sortedIndex++;
            }
            try {
                BufferedImage finalQRCodeImage = qrCodeService.multiQRCodes(qrCodedata, width, height, imageConfig, countPerRow);
                monoSinkConsumer.success(ResponseEntity.ok(utilsService.bufferedImageToBytes(finalQRCodeImage, "png")));
            } catch (WriterException | IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                monoSinkConsumer.error(e);
            }
        });
    }

    @PostMapping(value = "/detectQRCodes")
    public Mono<ResponseEntity<String>> detectQRCodes(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono
                .flatMap(filePart -> {
//                    filePart.transferTo(Paths.get("images/uplaodedImage.png")).subscribe();
                    return utilsService.getInputStream(filePart);
                })
                .flatMap(inputStream -> {
                    try {
                        ArrayList<String> codes = qrCodeService.detectQRCodes(inputStream);
                        System.out.println("[QR Codes Detected] : " + codes.size());
                        codes.forEach(System.out::println);
                        String combinedQRCode = compressionService.lz4Uncompress(utilsService.combineQRCodes(codes));
                        return Mono.just(ResponseEntity.ok(combinedQRCode));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        return Mono.just(ResponseEntity.ok(""));
                    }
                });
    }

    @PostMapping(value = "/detectQRCodesVideo")
    public Mono<ResponseEntity<String>> detectQRCodesFromVideo(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono
                .flatMap(filePart -> utilsService.getInputStream(filePart))
                .flatMap(inputStream -> {
                    String data = "";
                    try {
                        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream);
                        frameGrabber.start();
                        int count = 0;
                        int frameRate = (int) Math.ceil(frameGrabber.getFrameRate());
                        int frameLimit = frameRate + 10;
                        System.out.println("[Frame Rate] : " + frameRate);
                        while (true) {
                            Java2DFrameConverter c = new Java2DFrameConverter();
                            Frame f = frameGrabber.grab();
                            count++;
                            if (f == null) {
                                frameGrabber.stop();
                                System.out.println("DONE!!");
                                break;
                            }
                            BufferedImage bi = c.convert(f);
                            if (bi == null) {
                                continue;
                            }
                            System.out.println("[Frame] : " + count);
                            byte[] byteArray = utilsService.bufferedImageToBytes(bi, "jpg");
                            if (byteArray.length > 0) {
                                ArrayList<String> codes = new ArrayList<>();
                                try {
                                    codes = qrCodeService.detectQRCodes(new ByteArrayInputStream(byteArray));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("[QR Codes Detected] : " + codes.size());
                                codes.forEach(System.out::println);;
                                try {
                                    String combinedQRCode = compressionService.lz4Uncompress(utilsService.combineQRCodes(codes));
                                    data = combinedQRCode;
                                    System.out.println(combinedQRCode.substring(0, 100));
                                    break;
                                } catch (Exception e) {
                                    System.out.println("Decompress Failed");
                                }
                            }
                            System.out.println();
                        }
                        return Mono.just(ResponseEntity.ok(data));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        return Mono.just(ResponseEntity.ok(""));
                    }
                });
    }

}
