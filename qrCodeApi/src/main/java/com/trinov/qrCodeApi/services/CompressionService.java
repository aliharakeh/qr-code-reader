package com.trinov.qrCodeApi.services;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CompressionService {

    private final LZ4Factory factory = LZ4Factory.fastestInstance();

    public String lz4Compress(String dataToCompress) {
        byte[] data = dataToCompress.getBytes(StandardCharsets.UTF_8);
        final int decompressedLength = data.length;
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        int compressionLength = compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);
        return this.bytesToString(dataToCompress.length(), compressionLength, compressed);
    }

    public String lz4Uncompress(String compressedData) {
        String[] details = this.getDetails(compressedData);
        if (details.length < 3) {
            return "";
        }
        int decompressedLength = Integer.parseInt(details[0]);
        int compressionLength = Integer.parseInt(details[1]);
        byte[] compressionData = Base64.getDecoder().decode(details[2]);
//        byte[] compressionData = details[1].getBytes(StandardCharsets.ISO_8859_1);
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();
        byte[] uncompressed = new byte[decompressedLength];
        decompressor.decompress(compressionData, 0, compressionLength, uncompressed, 0);
        return new String(uncompressed, StandardCharsets.UTF_8);
    }

    private String bytesToString(int dataLength, int compressionLength, byte[] compressedData) {
        byte[] actualCompressedData = new byte[compressionLength];
        System.arraycopy(compressedData, 0, actualCompressedData, 0, compressionLength);
        StringBuilder res = new StringBuilder();
        res.append(dataLength).append(",");
        res.append(compressionLength).append(",");
        res.append(Base64.getEncoder().encodeToString(actualCompressedData));
//        res.append(new String(actualCompressedData, StandardCharsets.ISO_8859_1));
        return res.toString();
    }

    private String[] getDetails(String compressedString) {
        return compressedString.split(",");
    }
}
