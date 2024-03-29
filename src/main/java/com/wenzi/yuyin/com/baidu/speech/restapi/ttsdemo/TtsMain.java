package com.wenzi.yuyin.com.baidu.speech.restapi.ttsdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import com.wenzi.yuyin.com.baidu.speech.restapi.common.ConnUtil;
import com.wenzi.yuyin.com.baidu.speech.restapi.common.DemoException;
import com.wenzi.yuyin.com.baidu.speech.restapi.common.TokenHolder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TtsMain {

//	 public static void main(String[] args) throws IOException, DemoException {
//	 (new TtsMain()).run();
//	 }
	int i = 0;// 文件编号


	// 填写网页上申请的appkey 如 $apiKey="g8eBUMSokVB1BHGmgxxxxxx"
	private final String appKey = "vrCp2lcQdTzpGPsP0F8NijxW";

	// 填写网页上申请的APP SECRET 如 $secretKey="94dc99566550d87f8fa8ece112xxxxx"
	private final String secretKey = "09d0TG4sL73oP3Sd25hZy8nFjbavAzol";

	// text 的内容为"欢迎使用百度语音合成"的urlencode,utf-8 编码
	// 可以百度搜索"urlencode"
	private String text = "必定是嘎嘣覅带";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	// 发音人选择, 0为普通女声，1为普通男生，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女声
	private final int per = 3;
	// 语速，取值0-15，默认为5中语速
	private final int spd = 4;
	// 音调，取值0-15，默认为5中语调
	private final int pit = 5;
	// 音量，取值0-9，默认为5中音量
	private final int vol = 5;

	// 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
	private final int aue = 6;

	public final String url = "http://tsn.baidu.com/text2audio"; // 可以使用https

	private String cuid = "1234567JAVA";

	public void run() throws IOException, DemoException {

		TokenHolder holder = new TokenHolder(appKey, secretKey, TokenHolder.ASR_SCOPE);
		holder.refresh();
		String token = holder.getToken();

		// 此处2次urlencode， 确保特殊字符被正确编码
		String params = "tex=" + ConnUtil.urlEncode(ConnUtil.urlEncode(text));
		params += "&per=" + per;
		params += "&spd=" + spd;
		params += "&pit=" + pit;
		params += "&vol=" + vol;
		params += "&cuid=" + cuid;
		params += "&tok=" + token;
		params += "&aue=" + aue;
		params += "&lan=zh&ctp=1";
		System.out.println(url + "?" + params); // 反馈请带上此url，浏览器上可以测试
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setConnectTimeout(5000);
		PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
		printWriter.write(params);
		printWriter.close();
		String contentType = conn.getContentType();
		if (contentType.contains("audio/")) {
			byte[] bytes = ConnUtil.getResponseBytes(conn);
			String format = getFormat(aue);
			i++;
			String filename = String.valueOf(i);
			File file = new File(filename + "." + format); // 打开mp3文件即可播放
			// System.out.println( file.getAbsolutePath());
			FileOutputStream os = new FileOutputStream(file);
			os.write(bytes);
			os.close();
			System.out.println("audio file write to " + file.getAbsolutePath());
		} else {
			System.err.println("ERROR: content-type= " + contentType);
			String res = ConnUtil.getResponseString(conn);
			System.err.println(res);
		}
	}

	// 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
	private String getFormat(int aue) {
		String[] formats = { "mp3", "pcm", "pcm", "wav" };
		return formats[aue - 3];
	}
}
