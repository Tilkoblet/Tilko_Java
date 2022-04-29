package UnitTest.kr.or.hira;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class 십일대질병의심여부조회내역_건강보험심사평가원 {

    String apiHost	= "https://api.tilko.net/";
    String apiKey	= "";

    
    // RSA 암호화 함수
    public static String rsaEncrypt(String rsaPublicKey, byte[] aesKey) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String encryptedData				= null;
        
        KeyFactory keyFactory				= KeyFactory.getInstance("RSA");
        byte[] keyBytes						= Base64.getDecoder().decode(rsaPublicKey.getBytes("UTF-8"));
        X509EncodedKeySpec spec				= new X509EncodedKeySpec(keyBytes);
        PublicKey fileGeneratedPublicKey	= keyFactory.generatePublic(spec);
        RSAPublicKey key					= (RSAPublicKey)(fileGeneratedPublicKey);

        // 만들어진 공개키객체를 기반으로 암호화모드로 설정하는 과정
        Cipher cipher						= Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // 평문을 암호화하는 과정
        byte[] byteEncryptedData			= cipher.doFinal(aesKey);
        
        // Base64로 인코딩
        encryptedData						= new String(Base64.getEncoder().encodeToString(byteEncryptedData));

        return encryptedData;
    }

    
    // RSA 공개키(Public Key) 조회 함수
    public String getPublicKey() throws IOException, ParseException {
        OkHttpClient client		= new OkHttpClient();
        
        Request request			= new Request.Builder()
                                    .url(apiHost + "/api/Auth/GetPublicKey?APIkey=" + apiKey)
                                    .header("Content-Type", "application/json").build();

        Response response		= client.newCall(request).execute();
        String responseStr		= response.body().string();

        JSONParser jsonParser	= new JSONParser();
        JSONObject jsonObject	= (JSONObject) jsonParser.parse(responseStr);
        
        String rsaPublicKey		= (String) jsonObject.get("PublicKey");

        return rsaPublicKey;
    }


    public static void main(String[] args) throws IOException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        십일대질병의심여부조회내역_건강보험심사평가원 tc             = new 십일대질병의심여부조회내역_건강보험심사평가원();

        
        // RSA Public Key 조회
        String rsaPublicKey		= tc.getPublicKey();
        System.out.println("rsaPublicKey: " + rsaPublicKey);
        
        
        // AES Secret Key 및 IV 생성
        byte[] aesKey			= new byte[16];
        new Random().nextBytes(aesKey);
        
        byte[] aesIv			= new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

        
        // AES Key를 RSA Public Key로 암호화
        String aesCipherKey		= rsaEncrypt(rsaPublicKey, aesKey);
        System.out.println("aesCipherKey: " + aesCipherKey);
        

        // API URL 설정
        // HELP: https://tilko.net/Help/Api/POST-api-apiVersion-Hira-SuspectedDiseasesGet
        String url				= tc.apiHost + "api/v1.0/Hira/SuspectedDiseasesGet";
        

        // API 요청 파라미터 설정
        JSONObject json			= new JSONObject();
        json.put("MedicineCodeList", "__VALUE__");

        
        // API 호출
        OkHttpClient client		= new OkHttpClient();
        
        Request request			= new Request.Builder()
                                    .url(url)
                                    .addHeader("API-KEY"			, tc.apiKey)
                                    .addHeader("ENC-KEY"			, aesCipherKey)
                                    .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toJSONString())).build();

        Response response		= client.newCall(request).execute();
        String responseStr		= response.body().string();
        System.out.println("responseStr: " + responseStr);

        
    }

}
