package thesmith.realtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import thesmith.realtime.model.Video;
import thesmith.realtime.service.VideoService;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@Controller
public class RealtimeController {

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

    @RequestMapping(value = "/latest/send")
    public void sendLatest() {
        Video video = videoService.videoToSend();

        if (video != null) {
            JID jid = new JID("ben.thesmith@gmail.com");
            String body = video.getTitle() + " - " + video.getUri();
            Message msg = new MessageBuilder().withRecipientJids(jid).withBody(body).asXml(false).build();

            boolean messageSent = false;
            XMPPService xmpp = XMPPServiceFactory.getXMPPService();
            if (xmpp.getPresence(jid).isAvailable()) {
                SendResponse status = xmpp.sendMessage(msg);
                messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
            }

            if (messageSent) {
                videoService.markSent(video);
            }
        }
    }
}
