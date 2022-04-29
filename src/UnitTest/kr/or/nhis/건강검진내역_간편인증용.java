package UnitTest.kr.or.nhis;

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


public class 건강검진내역_간편인증용 {

    String apiHost	= "https://api.tilko.net/";
    String apiKey	= "";

    
    // AES 암호화 함수
    public String aesEncrypt(byte[] key, byte[] iv, byte[] plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher						= Cipher.getInstance("AES/CBC/PKCS5Padding");	// JAVA의 PKCS5Padding은 PKCS7Padding과 호환
        SecretKeySpec keySpec				= new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec				= new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] byteEncryptedData			= cipher.doFinal(plainText);
        
        // Base64로 인코딩
        String encryptedData				= new String(Base64.getEncoder().encodeToString(byteEncryptedData));
        
        return encryptedData;
    }

    
    // AES 암호화 함수
    public String aesEncrypt(byte[] key, byte[] iv, String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher						= Cipher.getInstance("AES/CBC/PKCS5Padding");	// JAVA의 PKCS5Padding은 PKCS7Padding과 호환
        SecretKeySpec keySpec				= new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec				= new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] byteEncryptedData			= cipher.doFinal(plainText.getBytes("UTF-8"));
        
        // Base64로 인코딩
        String encryptedData				= new String(Base64.getEncoder().encodeToString(byteEncryptedData));
        
        return encryptedData;
    }

    
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


    public static void main(String[] args) throws IOException, ParseException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        건강검진내역_간편인증용 tc             = new 건강검진내역_간편인증용();

        
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
        // HELP: https://tilko.net/Help/Api/POST-api-apiVersion-NhisSimpleAuth-Ggpab003M0105
        String url				= tc.apiHost + "api/v1.0/NhisSimpleAuth/Ggpab003M0105";
        
        
        // 간편인증 요청 후 받은 값 정리
        JSONObject reqData		= new JSONObject();
        reqData.put("CxId"					, "");
        reqData.put("PrivateAuthType"		, "");
        reqData.put("ReqTxId"				, "");
        reqData.put("Token"					, "");
        reqData.put("TxId"					, "");
        reqData.put("UserName"				, "");
        reqData.put("BirthDate"				, "");
        reqData.put("UserCellphoneNumber"	, "");
        

        // API 요청 파라미터 설정
        JSONObject json			= new JSONObject();

        // 위에서 정의한 reqData 객체를 활용해보세요.
        json.put("CxId"                 , (String) reqData.get("CxId"));
        json.put("PrivateAuthType"      , (String) reqData.get("PrivateAuthType"));
        json.put("ReqTxId"              , (String) reqData.get("ReqTxId"));
        json.put("Token"                , (String) reqData.get("Token"));
        json.put("TxId"                 , (String) reqData.get("TxId"));
        json.put("UserName"             , tc.aesEncrypt(aesKey, aesIv, (String) reqData.get("UserName")));
        json.put("BirthDate"            , tc.aesEncrypt(aesKey, aesIv, (String) reqData.get("BirthDate")));
        json.put("UserCellphoneNumber"  , tc.aesEncrypt(aesKey, aesIv, (String) reqData.get("UserCellphoneNumber")));
        json.put("기타필요한파라미터"       , tc.aesEncrypt(aesKey, aesIv, (String) reqData.get("UserName")));
        

        // API 호출
        OkHttpClient client		= new OkHttpClient.Builder()
                                    .connectTimeout(30, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .writeTimeout(30, TimeUnit.SECONDS)
                                    .build();
        
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
