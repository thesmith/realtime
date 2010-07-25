package thesmith.realtime.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import thesmith.realtime.model.User;
import thesmith.realtime.model.Video;
import thesmith.realtime.service.UserService;
import thesmith.realtime.service.VideoService;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.MessageType;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

@Controller
public class RealtimeController {
    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;
    
    private final Log logger = LogFactory.getLog(this.getClass());

    @RequestMapping(value = "/latest/retrieve")
    public String saveLatestVideos() {
        for (Video video : videoService.retrieveLatestVideos()) {
            if (null == videoService.get(video.getUri())) {
                logger.info("Persisting video: "+video.getUri());
                videoService.save(video);
            }
        }
        
        return "index/success";
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
    public String sendLatest() {
        Queue queue = QueueFactory.getDefaultQueue();
        Video video = videoService.videoToSend();

        if (video != null) {
            logger.info("Sending video: "+video.getUri());
            if (sendMessage(video)) {
                logger.info("Marking video sent: "+video.getUri());
                videoService.markSent(video);
            }
        }
        queue.add(url("/latest/send"));
        
        return "index/success";
    }
    
    @RequestMapping(value = "/register")
    public String register(@RequestParam String jid) {
        User user = userService.get(jid);
        if (user == null) {
            user = new User();
            user.setGid(jid);
            userService.save(user);
            
            logger.info("Registered user: "+user.getGid());
        }
        
        return "index/success";
    }

    private boolean sendMessage(Video video) {
        XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        boolean messagesSent = false;

        for (JID jid : getJids()) {
            Message msg = new MessageBuilder()
                    .withRecipientJids(jid)
                    .withBody(video.toString())
                    .withMessageType(MessageType.CHAT).build();
            
            boolean messageSent = false;
            if (xmpp.getPresence(jid).isAvailable()) {
                try {
                    SendResponse status = xmpp.sendMessage(msg);
                    messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
                } catch (Exception e) {
                    logger.error(msg);
                    logger.error(e);
                }
            } else {
                logger.info("Couldn't send video "+video.getUri()+" as "+jid.getId()+" isn't present");
            }
            
            if (!messageSent) {
                logger.warn("Message for "+video.getUri()+" failed to send");
//                User user = userService.get(jid.getId());
//                if (user != null) {
//                    userService.delete(user);
//                }
            } else {
                messagesSent = true;
            }
        }

        return messagesSent;
    }

    private List<JID> getJids() {
        List<JID> jids = Lists.newArrayList();
        for (User user : userService.get()) {
            jids.add(new JID(user.getGid()));
        }
        return jids;
    }
}
