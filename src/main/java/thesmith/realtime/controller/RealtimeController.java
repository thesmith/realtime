package thesmith.realtime.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import thesmith.realtime.model.Video;
import thesmith.realtime.service.VideoService;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.MessageType;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@Controller
public class RealtimeController {
    private JID jid = new JID("ben.thesmith@gmail.com");

    @Autowired
    private VideoService videoService;

    @RequestMapping(value = "/latest/retrieve")
    public void saveLatestVideos() {
        for (Video video : videoService.retrieveLatestVideos()) {
            if (null == videoService.get(video.getUri())) {
                videoService.save(video);
            }
        }
    }

    @RequestMapping(value = "/latest")
    public String getLatestVideos(ModelMap model) {
        model.put("latest", videoService.videosToSend(10));

        return "index/lastest";
    }
    
    @RequestMapping(value = "/")
    public String index() {
        return "index/index";
    }

    @RequestMapping(value = "/latest/send")
    public void sendLatest() {
        Video video = videoService.videoToSend();

        if (video != null) {
            Message msg = new MessageBuilder().withRecipientJids(jid)
                    .withBody(video.toJson())
                    .withMessageType(MessageType.CHAT).build();

            if (sendMessage(msg)) {
                videoService.markSent(video);
            }
        }
    }
    
    @RequestMapping(value = "/send")
    public void send(HttpServletResponse response) {
        Video video = new Video();
        video.setTitle("Glee - Episode 1");
        video.setDesc("It's Glee.");
        video.setUri("http://channel4.com/programmes/glee");
        video.setImage("http://www.channel4.com/assets/programmes/images/glee/series-1/episode-17/953dc28f-20d2-48e8-8147-0a74fda872ab_625x352.jpg");
        video.setLink("http://www.channel4.com/programmes/glee/4od#3069702");
        
        Message msg = new MessageBuilder().withRecipientJids(jid)
                .withBody(video.toJson())
                .withMessageType(MessageType.CHAT).build();
        response.setStatus(sendMessage(msg) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    private boolean sendMessage(Message msg) {
        boolean messageSent = false;
        XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        if (xmpp.getPresence(jid).isAvailable()) {
            try {
                SendResponse status = xmpp.sendMessage(msg);
                messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
            } catch (Exception e) {
                System.err.println(msg.getBody());
                e.printStackTrace();
            }
        }
        
        return messageSent;
    }
}
