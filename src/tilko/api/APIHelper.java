package tilko.api;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tilko.api.models.RsaPublicKey;

/**
  * @FileName : APIHelper.java
  * @Project : TilkoSampleSource
  * @Date : 2020. 8. 13.
  * @작성자 : Tilko.net
  * @변경이력 :
  * @프로그램 설명 :	API 요청 Helper Class
  */
public class APIHelper {
	static final Logger _logger = LoggerFactory.getLogger(APIHelper.class);

	//	Tilko.net에서 발급된 공용키
	private static String  _pubUrl = "https://api.tilko.net/api/Auth/GetPublicKey?ApiKey=";
	//	API Address : 건강보험공단(직장보험료 조회)
	private static String _companyInsuranceUrl = "https://api.tilko.net/api/v1.0/nhis/jpzaa00110";
	//	API Address : 건강보험공단(건강보험료납부내역)
	private static String _paymentUrl = "https://api.tilko.net/api/v1.0/nhis/jpaca00101/geongangboheom";
	//	API Address : 내가 먹는 약
	private static String _myDrugUrl = "https://api.tilko.net/api/v1.0/hira/hiraa050300000100";
	//	Tilko.net의 API 와 통신간 파라미터에 담긴 사용자의 개인정보를 암호화하기 위한 AES 알고리즘
	private AES _aes;
	//	Tilko.net 에 서 발급된 개인 apiKey
	private String _apiKey;


	public APIHelper(String apiKey){
		this._apiKey		= apiKey;

		//	AES 초기화
		this._aes			= new AES();
		byte[] _aesPlainKey	= new byte[16];
		new Random().nextBytes(_aesPlainKey);

		this._aes.setKey(_aesPlainKey);
		this._aes.setIv(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
	}

	/**
	  * @Method Name : getAESPlainKey
	  * @작성일 : 2020. 8. 13.
	  * @작성자 : Tilko.net
	  * @변경이력 :
	  * @Method 설명 : AES키 호출
	  * @return : AES키
	  */
	public byte[] getAESPlainKey() {

		return this._aes.getKey();
	}

	/**
	  * @Method Name : getRSAPubKey
	  * @작성일 : 2020. 8. 13.
	  * @작성자 : tilko.net
	  * @변경이력 :
	  * @Method 설명 :RSA공개키 요청 API호출
	  * @return : RSA공개키
	  * @throws IOException
	  */
	public RsaPublicKey getRSAPubKey() throws IOException {

		OkHttpClient _client	= new OkHttpClient();
		Request _request		= new Request.Builder()
												.url(_pubUrl + this._apiKey)
												.header("Content-Type", "application/json")
												.build();
		Response _response		= _client.newCall(_request).execute();
		String _responseStr		= _response.body().string();
		RsaPublicKey _pubKey	= (RsaPublicKey) new Gson().fromJson(_responseStr, RsaPublicKey.class);

		_logger.info("========= RSA KEY REQUEST =========");
		_logger.info("Response(RSA) :" + _responseStr);
		_logger.info("APIKEY:" + _pubKey.getApiKey());
		_logger.info("RSA PublicKey:" + _pubKey.getPublicKey());
		_logger.info("Message:" + _pubKey.getMessage());

		return _pubKey;
	}

	/**
	 * @Method Name : getCompanyInsurance
	 * @작성일 : 2021. 9. 24.
	 * @작성자 : Tilko.net
	 * @변경이력 :
	 * @Method 설명 : 직장보험료 조회 API 호출
	 * @param aesCipherKey : ENC-Key
	 * @param certFilePath : 인증서 공용키
	 * @param keyFilePath : 인증서 개인키
	 * @param txtCertPassword : 인증서 암호
	 * @param year : 검색년도(yyyy)
	 * @return : 결과
	 * @throws IOException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	  */
	public String getCompanyInsurance(byte[] aesCipherKey, String certFilePath, String keyFilePath, String txtCertPassword, String year) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		_logger.info(certFilePath);
		_logger.info(keyFilePath);
		_logger.info(txtCertPassword);
		_logger.info(year);

		byte[] _certCipherBytes				= this._aes.Encrypt(Util.FileToByteArray(certFilePath));
		byte[] _keyCipherBytes				= this._aes.Encrypt(Util.FileToByteArray(keyFilePath));
		byte[] _passwordCipherBytes			= this._aes.Encrypt(txtCertPassword.getBytes("US-ASCII"));

		//	파라미터 셋팅
		HashMap<String, String> _bodyMap	= new HashMap<String, String>();
		_bodyMap.put("CertFile", Util.base64Encode(_certCipherBytes));
		_bodyMap.put("KeyFile", Util.base64Encode(_keyCipherBytes));
		_bodyMap.put("CertPassword", Util.base64Encode(_passwordCipherBytes));
		_bodyMap.put("Year", year);

		RequestBody _body					= RequestBody.create
												(
													MediaType.parse("application/json; charset=utf-8"),
													new Gson().toJson(_bodyMap)
												);

		OkHttpClient _client				= new OkHttpClient().newBuilder().build();
		Request _request					= new Request.Builder()
															.url(APIHelper._companyInsuranceUrl)
															.post(_body)
															.addHeader("Content-Type", "application/json")
															.addHeader("API-Key", this._apiKey)
															.addHeader("ENC-Key", Util.base64Encode(aesCipherKey))
															.build();

		Response _response					= _client.newCall(_request).execute();
		String _responseStr					= _response.body().string();

		_logger.info("========= CompanyInsurance REQUEST =========");
		_logger.info("Response : " + _responseStr);
		_logger.info("API-Key: " +  this._apiKey);
		_logger.info("ENC-Key: {}", Util.base64Encode(aesCipherKey));

		return _responseStr;
	}
	
	
	/**
	 * @Method Name : getPaymentList
	 * @작성일 : 2020. 8. 13.
	 * @작성자 : Tilko.net
	 * @변경이력 :
	 * @Method 설명 : 건강보험료납부내역 API 호출
	 * @param aesCipherKey : ENC-Key
	 * @param certFilePath : 인증서 공용키
	 * @param keyFilePath : 인증서 개인키
	 * @param txtIdentityNumber : 주민등록번호(202008131234567)
	 * @param txtCertPassword : 인증서 암호
	 * @param year : 검색년도(yyyy)
	 * @param startMonth : 검색 시작 월(MM)
	 * @param endMonth : 검색 종료 월(MM)
	 * @return : 결과
	 * @throws IOException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public String getPaymentList(byte[] aesCipherKey, String certFilePath, String keyFilePath, String txtIdentityNumber, String txtCertPassword, String year, String startMonth, String endMonth) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
		_logger.info(certFilePath);
		_logger.info(keyFilePath);
		_logger.info(txtIdentityNumber);
		_logger.info(txtCertPassword);
		
		byte[] _certCipherBytes				= this._aes.Encrypt(Util.FileToByteArray(certFilePath));
		byte[] _keyCipherBytes				= this._aes.Encrypt(Util.FileToByteArray(keyFilePath));
		byte[] _identityCipherBytes			= this._aes.Encrypt(txtIdentityNumber.replace("-", "").getBytes("US-ASCII"));
		byte[] _passwordCipherBytes			= this._aes.Encrypt(txtCertPassword.getBytes("US-ASCII"));
		
		//	파라미터 셋팅
		HashMap<String, String> _bodyMap	= new HashMap<String, String>();
		_bodyMap.put("CertFile", Util.base64Encode(_certCipherBytes));
		_bodyMap.put("KeyFile", Util.base64Encode(_keyCipherBytes));
		_bodyMap.put("IdentityNumber", Util.base64Encode(_identityCipherBytes));
		_bodyMap.put("CertPassword", Util.base64Encode(_passwordCipherBytes));
		_bodyMap.put("Year", year);
		_bodyMap.put("StartMonth", startMonth);
		_bodyMap.put("EndMonth", endMonth);
		
		RequestBody _body					= RequestBody.create
				(
						MediaType.parse("application/json; charset=utf-8"),
						new Gson().toJson(_bodyMap)
						);
		
		OkHttpClient _client				= new OkHttpClient().newBuilder().build();
		Request _request					= new Request.Builder()
				.url(APIHelper._paymentUrl)
				.post(_body)
				.addHeader("Content-Type", "application/json")
				.addHeader("API-Key", this._apiKey)
				.addHeader("ENC-Key", Util.base64Encode(aesCipherKey))
				.build();
		
		Response _response					= _client.newCall(_request).execute();
		String _responseStr					= _response.body().string();
		
		_logger.info("========= PaymentList REQUEST =========");
		_logger.info("Response : " + _responseStr);
		_logger.info("API-Key: " +  this._apiKey);
		_logger.info("ENC-Key: {}", Util.base64Encode(aesCipherKey));
		
		return _responseStr;
	}


	/**
	  * @Method Name : getMYDrug
	  * @작성일 : 2020. 8. 13.
	  * @작성자 : Tilko.net
	  * @변경이력 :
	  * @Method 설명 : 내가 먹는 약 API 호출
	  * @param aesCipherKey : ENC-Key
	  * @param certFilePath : 인증서 공개키
	  * @param keyFilePath : 인증서 개인키
	  * @param txtIdentityNumber : 주민등록번호
	  * @param txtCertPassword : 인증서 비밀번호
	  * @param telecomCompany : 통신사 코드 (통신사 SKT : 0 / KT : 1 / LGT : 2 / SKT알뜰폰 : 3 / KT알뜰폰 : 4 / LGT알뜰폰 : 5 / NA : 6)
	  * @param cellphoneNumber : 휴대폰 번호
	  * @return : 결과
	  * @throws InvalidKeyException
	  * @throws NoSuchAlgorithmException
	  * @throws NoSuchPaddingException
	  * @throws InvalidAlgorithmParameterException
	  * @throws IllegalBlockSizeException
	  * @throws BadPaddingException
	  * @throws IOException
	  */
	public String getMYDrug(byte[] aesCipherKey, String certFilePath, String keyFilePath, String txtIdentityNumber, String txtCertPassword, String telecomCompany, String cellphoneNumber) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

		byte[] _certCipherBytes				= this._aes.Encrypt(Util.FileToByteArray(certFilePath));
		byte[] _keyCipherBytes				= this._aes.Encrypt(Util.FileToByteArray(keyFilePath));
		byte[] _identityCipherBytes			= this._aes.Encrypt(txtIdentityNumber.replace("-", "").getBytes("US-ASCII"));
		byte[] _passwordCipherBytes			= this._aes.Encrypt(new String(txtCertPassword).getBytes("US-ASCII"));
		byte[] _cellphonNumberCipherByte	= this._aes.Encrypt(cellphoneNumber.getBytes("US-ASCII"));

		_logger.info("========= MY_Drug REQUEST =========");

		HashMap<String, String> _bodyMap = new HashMap<String, String>();
		_bodyMap.put("CertFile", Util.base64Encode(_certCipherBytes));
		_bodyMap.put("KeyFile", Util.base64Encode(_keyCipherBytes));
		_bodyMap.put("IdentityNumber", Util.base64Encode(_identityCipherBytes));
		_bodyMap.put("CertPassword", Util.base64Encode(_passwordCipherBytes));
		_bodyMap.put("TelecomCompany", telecomCompany);
		_bodyMap.put("CellphoneNumber", Util.base64Encode(_cellphonNumberCipherByte));

		RequestBody _body					= RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(_bodyMap));

		OkHttpClient _client				= new OkHttpClient().newBuilder().build();
		Request _request					= new Request.Builder()
													.url(APIHelper._myDrugUrl)
													.post(_body)
													.addHeader("Content-Type", "application/json")
													.addHeader("API-Key", this._apiKey)
													.addHeader("ENC-Key",Util.base64Encode(aesCipherKey))
													.build();

		Response _response					= _client.newCall(_request).execute();
		String _responseStr					= _response.body().string();

		_logger.info("response:" +  _response.toString());
		_logger.info("responseStr:" +  _responseStr.toString());

		_logger.info("========= MY_Drug REQUEST =========");
		_logger.info("Response :" + _responseStr);
		_logger.info("API-Key:" +  _apiKey);
		_logger.info("ENC-Key:{}", Util.base64Encode(aesCipherKey));
		_logger.info("CertFile:" + Util.base64Encode(_certCipherBytes));
		_logger.info("KeyFile:" + Util.base64Encode(_keyCipherBytes));
		_logger.info("IdentityNumber:" + Util.base64Encode(_identityCipherBytes));
		_logger.info("CertPassword:" + Util.base64Encode(_passwordCipherBytes));

		return _responseStr;
	}
}