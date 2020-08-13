package tilko.api.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tilko.api.APIHelper;
import tilko.api.Util;
import tilko.api.models.RsaPublicKey;

public class UI {
	static final Logger logger = LoggerFactory.getLogger(UI.class);

	public JFrame frame;
	private JTextField _txtAPI_KEY;
	private JPanel panel_1;
	private JLabel lblNewLabel_1;
	private JTextField _txtCertPath;
	private JPanel panel_2;
	private JLabel lblEndpoint;
	private JPanel panel_3;
	private JButton _btnFind;
	private JLabel lblNewLabel_2;
	private JTextField _txtIdentityNumber;
	private JLabel lblNewLabel_4;
	private JComboBox _cmbEndPoint;
	private JTextArea _txtResult;
	private JPanel panel_4;
	private JButton _btnOK;
	private JButton _btnCancel;
	private JLabel lblNewLabel_5;
	private JLabel lblNewLabel_6;
	private JPasswordField _txtCertPassword;


	/**
	 * Create the application.
	 */
	public UI() {
		initialize();
		initUIData();
	}

	// 각 데이터의 EndPoint를 정의합니다.
	public void initUIData() {
		this._cmbEndPoint.addItem(new ComboItem("건강보험공단(건강보험료납부내역)", "http://beta.api.tilko.net/api/v1.0/Nhis/PaymentList"));
		this._cmbEndPoint.addItem(new ComboItem("내가 먹는 약", "http://beta.api.tilko.net/api/v1.0/Hira/MyDrugList"));

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 704, 620);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 700, 0 };
		gridBagLayout.rowHeights = new int[] { 130, 200, 100, 250, 65, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 2.0, 2.0, 1.2, 2.5, 0.6, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		// 1.API키 입력
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "1. API\uD0A4 \uC785\uB825", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 100, 500, 0 };
		gbl_panel.rowHeights = new int[] { 26, 26, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblNewLabel = new JLabel("API 키");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);

		_txtAPI_KEY = new JTextField();
		GridBagConstraints gbc__txtAPI_KEY = new GridBagConstraints();
		gbc__txtAPI_KEY.insets = new Insets(0, 0, 5, 0);
		gbc__txtAPI_KEY.fill = GridBagConstraints.HORIZONTAL;
		gbc__txtAPI_KEY.gridx = 1;
		gbc__txtAPI_KEY.gridy = 0;
		panel.add(_txtAPI_KEY, gbc__txtAPI_KEY);
		_txtAPI_KEY.setColumns(10);

		lblNewLabel_5 = new JLabel("홈페이지(http://tilko.net) > 내정보 > API KEY 목록에서 API 키를 확인하고 입력해주세요.");
		lblNewLabel_5.setForeground(Color.RED);
		lblNewLabel_5.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblNewLabel_5.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel_5.gridx = 1;
		gbc_lblNewLabel_5.gridy = 1;
		panel.add(lblNewLabel_5, gbc_lblNewLabel_5);

		// 2.공인인증서 정보 입력
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
				"2. \uACF5\uC778\uC778\uC99D\uC11C \uC815\uBCF4 \uC785\uB825", TitledBorder.LEADING, TitledBorder.TOP,
				null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		frame.getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 100, 400, 100, 0 };
		gbl_panel_1.rowHeights = new int[] { 26, 26, 26, 26, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		lblNewLabel_1 = new JLabel("인증서 경로");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		panel_1.add(lblNewLabel_1, gbc_lblNewLabel_1);

		_txtCertPath = new JTextField();
		_txtCertPath.setColumns(10);
		_txtCertPath.setEditable(false);
		_txtCertPath.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc__txtCertPath = new GridBagConstraints();
		gbc__txtCertPath.fill = GridBagConstraints.BOTH;
		gbc__txtCertPath.insets = new Insets(0, 0, 5, 5);
		gbc__txtCertPath.gridx = 1;
		gbc__txtCertPath.gridy = 0;
		panel_1.add(_txtCertPath, gbc__txtCertPath);

		_btnFind = new JButton("찾기..");
		_btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 인증서 폴더 설정
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("인증서 폴더 선택");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
					System.out.println("getSelectedFile() : " + chooser.getSelectedFile());

					try {
						_txtCertPath.setText(chooser.getSelectedFile().getCanonicalPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		GridBagConstraints gbc__btnFind = new GridBagConstraints();
		gbc__btnFind.insets = new Insets(0, 0, 5, 0);
		gbc__btnFind.anchor = GridBagConstraints.NORTHWEST;
		gbc__btnFind.gridx = 2;
		gbc__btnFind.gridy = 0;
		panel_1.add(_btnFind, gbc__btnFind);

		lblNewLabel_6 = new JLabel("공인인증서는 대부분 C:\\Users\\[사용자명]\\AppData\\LocalLow\\NPKI\\yessign\\USER 폴더에 있습니다.");
		lblNewLabel_6.setForeground(Color.RED);
		lblNewLabel_6.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblNewLabel_6.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 1;
		gbc_lblNewLabel_6.gridy = 1;
		panel_1.add(lblNewLabel_6, gbc_lblNewLabel_6);

		lblNewLabel_2 = new JLabel("주민등록번호");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panel_1.add(lblNewLabel_2, gbc_lblNewLabel_2);

		_txtIdentityNumber = new JTextField();
		_txtIdentityNumber.setColumns(10);
		GridBagConstraints gbc__txtIdentityNumber = new GridBagConstraints();
		gbc__txtIdentityNumber.insets = new Insets(0, 0, 5, 5);
		gbc__txtIdentityNumber.fill = GridBagConstraints.HORIZONTAL;
		gbc__txtIdentityNumber.gridx = 1;
		gbc__txtIdentityNumber.gridy = 2;
		panel_1.add(_txtIdentityNumber, gbc__txtIdentityNumber);

		lblNewLabel_4 = new JLabel("인증서 비밀번호");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 3;
		panel_1.add(lblNewLabel_4, gbc_lblNewLabel_4);

		_txtCertPassword = new JPasswordField();
		GridBagConstraints gbc__txtCertPassword = new GridBagConstraints();
		gbc__txtCertPassword.insets = new Insets(0, 0, 0, 5);
		gbc__txtCertPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc__txtCertPassword.gridx = 1;
		gbc__txtCertPassword.gridy = 3;
		panel_1.add(_txtCertPassword, gbc__txtCertPassword);

		// 3.EndPoint 선택
		panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
				"3. EndPoint \uC120\uD0DD", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 2;
		frame.getContentPane().add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 100, 500, 0 };
		gbl_panel_2.rowHeights = new int[] { 26, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		lblEndpoint = new JLabel("EndPoint");
		GridBagConstraints gbc_lblEndpoint = new GridBagConstraints();
		gbc_lblEndpoint.fill = GridBagConstraints.VERTICAL;
		gbc_lblEndpoint.anchor = GridBagConstraints.EAST;
		gbc_lblEndpoint.insets = new Insets(0, 0, 0, 5);
		gbc_lblEndpoint.gridx = 0;
		gbc_lblEndpoint.gridy = 0;
		panel_2.add(lblEndpoint, gbc_lblEndpoint);

		_cmbEndPoint = new JComboBox();
		GridBagConstraints gbc__cmbEndPoint = new GridBagConstraints();
		gbc__cmbEndPoint.insets = new Insets(0, 0, 0, 5);
		gbc__cmbEndPoint.fill = GridBagConstraints.HORIZONTAL;
		gbc__cmbEndPoint.gridx = 1;
		gbc__cmbEndPoint.gridy = 0;
		panel_2.add(_cmbEndPoint, gbc__cmbEndPoint);

		// 4.데이터 결과
		panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null),
				"4. \uB370\uC774\uD130 \uACB0\uACFC", TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 3;
		frame.getContentPane().add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 100, 0 };
		gbl_panel_3.rowHeights = new int[] { 26, 0 };
		gbl_panel_3.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		_txtResult = new JTextArea();
		GridBagConstraints gbc__txtResult = new GridBagConstraints();
		gbc__txtResult.insets = new Insets(0, 0, 0, 5);
		gbc__txtResult.fill = GridBagConstraints.BOTH;
		gbc__txtResult.gridx = 0;
		gbc__txtResult.gridy = 0;
		panel_3.add(_txtResult, gbc__txtResult);

		panel_4 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 4;
		frame.getContentPane().add(panel_4, gbc_panel_4);

		_btnOK = new JButton("확 인");
		_btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					_btnOK.setEnabled(false);
					_btnCancel.setEnabled(false);


					String _certFilePath = _txtCertPath.getText() + File.separator + "signCert.der";
					String _keyFilePath = _txtCertPath.getText() + File.separator + "signPri.key";
					logger.info("certFilePath : {}", _certFilePath);
					logger.info("keyFilePath : {}", _keyFilePath);


					if (Util.isNullOrEmpty(_txtAPI_KEY.getText())) {
						throw new RuntimeException("API 키를 입력하세요.");
					}
					if (Util.isNullOrEmpty(_txtIdentityNumber.getText())) {
						throw new RuntimeException("주민등록번호를 입력하세요.");
					}
					if (Util.isNullOrEmpty(_txtCertPassword.getText())) {
						throw new RuntimeException("인증서 비밀번호를 입력하세요.");
					}
					if (Util.isNullOrEmpty(_txtCertPath.getText())) {
						throw new RuntimeException("인증서 경로를 선택하세요.");
					}
					if (!(new File(_certFilePath)).isFile()) {
						throw new RuntimeException("인증서 경로에 인증서 공개키 파일이 존재하지 않습니다.");
					}
					if (!(new File(_keyFilePath)).isFile()) {
						throw new RuntimeException("인증서 경로에 인증서 개인키 파일이 존재하지 않습니다.");
					}

					_txtResult.setText("");


					//RSA 공개키 요청
					APIHelper apiHelper = new APIHelper(_txtAPI_KEY.getText());
					RsaPublicKey _pubKey = apiHelper.getRSAPubKey();
					if(_pubKey.getMessage().equals("Error")) {
						throw new RuntimeException(_pubKey.getMessage());
					}


					//	API에 따른 분기
					ComboItem apiSelcted = (ComboItem)_cmbEndPoint.getSelectedItem();
					logger.info("API Selected : {}/{}", apiSelcted.key, apiSelcted.value);


					//	RSA공개로 AES키 암호화
					byte[] _aesCipherKey = Util.encodeByRSAPublicKey( apiHelper.getAESPlainKey(), _pubKey.getPublicKey());

					//	건강보험료 납부 내역 조회
					if(apiSelcted.key.equals("건강보험공단(건강보험료납부내역)")) {
						//건강보험료 납부 내역 조회
						String _yyyy				= "2019";
						String _sMM					= "01";
						String _eMM					= "12";

						String result = apiHelper.getPaymentList(_aesCipherKey, _certFilePath, _keyFilePath, _txtIdentityNumber.getText(), _txtCertPassword.getText(), _yyyy, _sMM, _eMM);
						_txtResult.setText(result);
					}else {
						//	내가 먹는 약 조회
						String _telecomCompany		= "0";				//	통신사 SKT : 0 / KT : 1 / LGT : 2 / SKT알뜰폰 : 3 / KT알뜰폰 : 4 / LGT알뜰폰 : 5 / NA : 6
						String _cellNumber			= "01012345678";	//	핸드폰번호 (01012345678)

						String result = apiHelper.getMYDrug(_aesCipherKey, _certFilePath, _keyFilePath, _txtIdentityNumber.getText(), _txtCertPassword.getText(), _telecomCompany, _cellNumber);
						_txtResult.setText(result);
					}

				} catch (Exception e1) {
					logger.error("error:{}", e);
					JOptionPane.showMessageDialog(null, e1, "오류", JOptionPane.ERROR_MESSAGE);

				}finally {
					_btnOK.setEnabled(true);
					_btnCancel.setEnabled(true);
				}

			}
		});
		panel_4.add(_btnOK);

		_btnCancel = new JButton("취 소");
		_btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel_4.add(_btnCancel);
	}

	class ComboItem {
		private String key;
		private String value;

		public ComboItem(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return key;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
}
