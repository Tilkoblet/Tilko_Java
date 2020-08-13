package tilko.api.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Test;

import junit.framework.TestCase;
import tilko.api.AES;
import tilko.api.APIHelper;
import tilko.api.Util;
import tilko.api.models.RsaPublicKey;

/**
 * @FileName : UnitTest.java
 * @Project : TilkoSampleSource
 * @Date : 2020. 8. 13.
 * @작성자 : Tilko.net
 * @변경이력 :
 * @프로그램 설명 : 테스트 모듈
 */
public class UnitTest extends TestCase {
	// Tilko.net 에서 발급된 API Key
	private static String _apiKey = "Tilko.net 에서 발급된 API Key";
	/*
	 * 인증서 경로, windows os 기준, 보통 c:\Users(사용자)\PC이름\Appdata\LocallLow\NPKI 폴더에 인증서가
	 * 위치합니다. yessign 에서 발급된 인증서의 예 EX:)
	 * c:\Users(사용자)\PC이름\Appdata\LocallLow\NPKI\yessign\USER\인증서폴더
	 */
	private static String _certPath = "C:\\Users\\PC이름\\AppData\\LocalLow\\NPKI\\yessign\\USER\\인증서폴더";

	/**
	 * @Method Name : PaymentListTest
	 * @작성일 : 2020. 8. 13.
	 * @작성자 : Tilko.net
	 * @변경이력 :
	 * @Method 설명 : 건강보험료 납부 내역 조회 테스트 메소드
	 * @throws Exception
	 */
	@Test
	public void PaymentListTest() throws Exception {

		APIHelper _apiHelper = new APIHelper(UnitTest._apiKey);
		RsaPublicKey _pubKey = _apiHelper.getRSAPubKey();

		// RSA공개로 AES키 암호화
		byte[] _aesCipherKey = Util.encodeByRSAPublicKey(_apiHelper.getAESPlainKey(), _pubKey.getPublicKey());

		// 건강보험료 납부 내역 조회
		String _txtIdentityNumber = "주민등록번호"; // 주민등록번호
		String _txtCertPassword = "인증서비밀번호"; // 인증서 비밀번호
		String _yyyy = "2019";
		String _sMM = "01";
		String _eMM = "12";

		String _result = _apiHelper.getPaymentList(_aesCipherKey, UnitTest._certPath + File.separator + "signCert.der",
				UnitTest._certPath + File.separator + "signPri.key", _txtIdentityNumber, _txtCertPassword, _yyyy, _sMM,
				_eMM);

		// 결과
		System.out.println("PaymentListTest - result:" + _result);

		assertEquals(_result.length() > 0, true);
	}

	/**
	 * @Method Name : MyDrugTest
	 * @작성일 : 2020. 8. 13.
	 * @작성자 : Tilko.net
	 * @변경이력 :
	 * @Method 설명 : 내가먹는약 조회 테스트
	 * @throws Exception
	 */
	@Test
	public void MyDrugTest() throws Exception {

		APIHelper _apiHelper = new APIHelper(UnitTest._apiKey);
		RsaPublicKey _pubKey = _apiHelper.getRSAPubKey();

		// RSA공개로 AES키 암호화
		byte[] _aesCipherKey = Util.encodeByRSAPublicKey(_apiHelper.getAESPlainKey(), _pubKey.getPublicKey());

		// 내가 먹는 약 조회
		String _txtIdentityNumber = "주민등록번호"; // 주민등록번호
		String _txtCertPassword = "인증서비밀번호"; // 인증서 비밀번호
		String _telecomCompany = "0"; // 통신사 SKT : 0 / KT : 1 / LGT : 2 / SKT알뜰폰 : 3 / KT알뜰폰 : 4 / LGT알뜰폰 : 5 / NA : 6
		String _cellNumber = "01012345678"; // 핸드폰번호 (01012345678)

		String _result = _apiHelper.getMYDrug(_aesCipherKey, UnitTest._certPath + File.separator + "signCert.der",
				UnitTest._certPath + File.separator + "signPri.key", _txtIdentityNumber, _txtCertPassword,
				_telecomCompany, _cellNumber);

		// 결과
		System.out.println("MyDrugTest - result:" + _result);

		assertEquals(_result.length() > 0, true);
	}

	/**
	 * @Method Name : AESEncryptTest
	 * @작성일 : 2020. 8. 13.
	 * @작성자 : Tilko.net
	 * @변경이력 :
	 * @Method 설명 : AES 암호화 테스트
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void AESEncryptTest() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			UnsupportedEncodingException {
		String plainText = "test";

		AES aes = new AES();
		aes.setKey(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00 });
		aes.setIv(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 });

		byte[] encText = aes.Encrypt(plainText.getBytes());

		System.out.println(
				"AESEncryptTest - base64 test:" + Base64.getEncoder().encodeToString(plainText.getBytes("UTF-8")));
		System.out.println("AESEncryptTest - plainText:" + plainText);
		System.out.println("AESEncryptTest - encText:" + Base64.getEncoder().encodeToString(encText));

		assertEquals(encText.length > 0, true);
		assertEquals(Base64.getEncoder().encodeToString(plainText.getBytes("UTF-8")), "dGVzdA==");
		assertEquals(Base64.getEncoder().encodeToString(encText), "qTEyaO8wodj/sAFhzipZdw==");
	}
}