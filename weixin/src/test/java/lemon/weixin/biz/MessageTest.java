package lemon.weixin.biz;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import lemon.shared.api.MmtAPI;
import lemon.weixin.WeiXin;
import lemon.weixin.bean.WeiXinConfig;
import lemon.weixin.bean.message.MusicMessage;
import lemon.weixin.bean.message.NewsMessage;
import lemon.weixin.bean.message.TextMessage;
import lemon.weixin.bean.message.VideoMessage;
import lemon.weixin.bean.message.VoiceMessage;
import lemon.weixin.biz.parser.MusicMsgParser;
import lemon.weixin.biz.parser.NewsMsgParser;
import lemon.weixin.biz.parser.TextMsgParser;
import lemon.weixin.biz.parser.VideoMsgParser;
import lemon.weixin.biz.parser.VoiceMsgParser;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(JUnit4.class)
public class MessageTest {
	private MmtAPI api;
	private final String Subscribe_msg = "Welcome to Subscribe Lemon Test.";
	private final String unsubscribe_msg = "You have unsubscribe Lemon Test, Thank you!";
	private final String TOKEN = "1230!)*!)*#)!*Q)@)!*";
	private final String MMT_TOKEN = "lemonxoewfnvowensofcewniasdmfo";
	
	@Before
	public void init() {
		String[] resource = { "classpath:spring-db.xml",
				"classpath:spring-dao.xml", "classpath:spring-service.xml" };
		ApplicationContext acx = new ClassPathXmlApplicationContext(resource);
		api = (MmtAPI) acx.getBean(MmtAPI.class);
		assertNotNull(api);
		WeiXin.init();
		WeiXinConfig cfg = new WeiXinConfig();
		cfg.setCust_id(100);
		cfg.setToken(TOKEN);
		cfg.setApi_url(MMT_TOKEN);
		cfg.setWx_account("lemon_test");
		cfg.setBiz_class("lemon.weixin.biz.LemonMessageBiz");
		cfg.setSubscribe_msg(Subscribe_msg);
		cfg.setUnsubscribe_msg(unsubscribe_msg);
		WeiXin.setConfig(cfg);
	}
	//FIXME use production data to test
	@Test
	public void parserMsgType() throws JDOMException, IOException{
		String msg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377241649729</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[hello,weixin, I am lemon.]]></Content></xml>";
		InputStream is = new ByteArrayInputStream(msg.getBytes());
		Document doc = new SAXBuilder().build(is);
		Element msgType = doc.getRootElement().getChild("MsgType");
		Assert.assertTrue("text".equals(msgType.getValue()));
	}
	@Test
	public void textMsgTest(){
		String txtMsg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377241649729</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[hello,weixin, I am lemon.]]></Content></xml>";
		String result = api.processMsg(MMT_TOKEN, txtMsg);
		TextMessage msg = new TextMsgParser().toMsg(result);
		assertEquals(msg.getContent(), "Lemon Text message replay.");
	}
	@Test
	public void subscribeTest(){
		String recvMsg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377682037695</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[subscribe]]></Event><EventKey><![CDATA[0dfsafkqwnriksdk]]></EventKey></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		TextMessage msg = new TextMsgParser().toMsg(result);
		assertEquals(msg.getContent(), Subscribe_msg);
	}
	
	@Test
	public void unsubscribe(){
		String recvMsg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377682037695</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[unsubscribe]]></Event><EventKey><![CDATA[0dfsafkqwnriksdk]]></EventKey></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		TextMessage msg = new TextMsgParser().toMsg(result);
		assertEquals(msg.getContent(), unsubscribe_msg);
	}
	@Test
	public void linkMsgTest(){
		String recvMsg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377753855909</CreateTime><MsgType><![CDATA[link]]></MsgType><MsgId>1024102410241024</MsgId><Title><![CDATA[Link \"TEST\" Title]]></Title><Description><![CDATA[Link DESC]]></Description><Url><![CDATA[http://www.163.com/s/a/d/f/a]]></Url></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		TextMessage msg = new TextMsgParser().toMsg(result);
		assertEquals(msg.getContent(), "Lemon Link message replay.");
	}
	
	@Test
	public void imageMsgTest(){
		String recvMsg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377754117379</CreateTime><MsgType><![CDATA[image]]></MsgType><MsgId>1024102410241024</MsgId><PicUrl><![CDATA[http://www.baidu.com/sadsaf]]></PicUrl></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		TextMessage msg = new TextMsgParser().toMsg(result);
		assertEquals(msg.getContent(), "Lemon Image message replay.");
	}
	
	@Test
	public void locationMsgTest(){
		String recvMsg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377754299991</CreateTime><MsgType><![CDATA[location]]></MsgType><MsgId>1024102410241024</MsgId><Location__X>23.134521</Location__X><Location__Y>113.358803</Location__Y><Scale>20</Scale><Label><![CDATA[I am here.<xml>\"sdf\"</xml>]]></Label></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		TextMessage msg = new TextMsgParser().toMsg(result);
		assertEquals(msg.getContent(), "Lemon Location message replay.");
	}
	
	@Test
	public void musicMsgTest(){
		String recvMsg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377754486787</CreateTime><MsgType><![CDATA[music]]></MsgType><MsgId>1024102410241024</MsgId><MusicUrl><![CDATA[http://music.baidu.com/a/a/d.mp3]]></MusicUrl><HQMusicUrl><![CDATA[HQmusic  ss s]]></HQMusicUrl></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		MusicMessage msg = new MusicMsgParser().toMsg(result);
		assertEquals(msg.getMusicUrl(), "nusic URL");
		assertEquals(msg.getHqMusicUrl(), "HQ music URL");
	}
	
	@Test
	public void newsMsgTest(){
		String recvMsg = "<xml><ToUserName><![CDATA[weixin]]></ToUserName><FromUserName><![CDATA[lemon]]></FromUserName><CreateTime>1377754656922</CreateTime><MsgType><![CDATA[news]]></MsgType><MsgId>1024102410241024</MsgId><ArticleCount>2</ArticleCount><Articles><item><Title><![CDATA[Title A1]]></Title><Description><![CDATA[DESC A1]]></Description><PicUrl><![CDATA[pic.taobao.com/aaas/asdf.jpg]]></PicUrl><Url><![CDATA[http://www.baidu.com]]></Url></item><item><Title><![CDATA[Title A2]]></Title><Description><![CDATA[DESC A2]]></Description><PicUrl><![CDATA[pic2.taobao.com/aaas/asdf222.jpg]]></PicUrl><Url><![CDATA[http://www.yousas.com]]></Url></item></Articles></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		NewsMessage msg = new NewsMsgParser().toMsg(result);
		assertEquals(msg.getArticleCount(), 2);
	}
	
	@Test
	public void videoMsgTest(){
		String recvMsg = "<xml><ToUserName><![CDATA[gh_de370ad657cf]]></ToUserName><FromUserName><![CDATA[ot9x4jpm4x_rBrqacQ8hzikL9D-M]]></FromUserName><CreateTime>1377961745</CreateTime><MsgType><![CDATA[video]]></MsgType><MediaId><![CDATA[Iy6-lX7dSLa45ztf6AVdjTDcHTWLk3C80VHMGi40HfI1CnpPqixCb6FUJ2ZG4wNd]]></MediaId><ThumbMediaId><![CDATA[gJNpZwX41lZ651onCiBzaYkYOTrqDC_v6oBY9TNocYCMWHG7Zsp67-jq-NRQS1Uk]]></ThumbMediaId><MsgId>5918300629914091537</MsgId></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		VideoMessage msg = new VideoMsgParser().toMsg(result);
		assertEquals(msg.getThumbMediaId(), "gJNpZwX41lZ651onCiBzaYkYOTrqDC_v6oBY9TNocYCMWHG7Zsp67-jq-NRQS1Uk");
		assertEquals(msg.getMediaId(), "Iy6-lX7dSLa45ztf6AVdjTDcHTWLk3C80VHMGi40HfI1CnpPqixCb6FUJ2ZG4wNd");
	}
	
	@Test
	public void voiceMsgTest(){
		String recvMsg = "<xml><ToUserName><![CDATA[gh_de370ad657cf]]></ToUserName><FromUserName><![CDATA[ot9x4jpm4x_rBrqacQ8hzikL9D-M]]></FromUserName><CreateTime>1378040271</CreateTime><MsgType><![CDATA[voice]]></MsgType><MediaId><![CDATA[PG_BHErDUcBylPzSDZHgpGa34axYmbe3_HGaQ7VCYQa_ihn9ON8lpevua76VMsHj]]></MediaId><Format><![CDATA[amr]]></Format><MsgId>5918637896515977279</MsgId><Recognition><![CDATA[]]></Recognition></xml>";
		String result = api.processMsg(MMT_TOKEN, recvMsg);
		VoiceMessage msg = new VoiceMsgParser().toMsg(result);
		assertEquals(msg.getFormat(), "amr");
		assertEquals(msg.getMediaId(), "PG_BHErDUcBylPzSDZHgpGa34axYmbe3_HGaQ7VCYQa_ihn9ON8lpevua76VMsHj");
		assertEquals(msg.getRecognition(), "");
	}
	
}
