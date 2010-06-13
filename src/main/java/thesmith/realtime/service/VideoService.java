package thesmith.realtime.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import thesmith.realtime.model.Video;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

@Transactional
@Component
public class VideoService {

    @PersistenceContext
    private EntityManager em;

    public List<Video> retrieveLatestVideos() {
        List<Video> videos = Lists.newArrayList();
        try {
            URL url = new URL(
                            "http://api.uriplay.org/2.0/items.json?playlist.uri=http://uriplay.org/hotness/twitter&location.transportType=link");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            StringBuffer json = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JSONObject uriplay = new JSONObject(json.toString());
            if (uriplay.has("items")) {
                JSONArray items = uriplay.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    Video video = new Video();

                    if (!item.has("title") || !item.has("description") || !item.has("thumbnail") || !item.has("uri")) {
                        continue;
                    }
                    
                    String title = "";
                    if (item.has("brand")) {
                        JSONObject brand = item.getJSONObject("brand");
                        title = brand.getString("title") + " - ";
                    }
                    
                    video.setTitle(title+item.getString("title"));
                    video.setDesc(item.getString("description"));
                    video.setImage(item.has("image") ? item.getString("image") : item.getString("thumbnail"));
                    video.setUri(item.getString("uri"));

                    String link = null;
                    if (item.has("locations")) {
                        JSONObject location = item.getJSONArray("locations").getJSONObject(0);
                        link = location.getString("uri");
                    }
                    video.setLink(link);

                    video.setCreated(new Date());
                    video.setSent(false);
                    videos.add(video);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return videos;
    }

    public void save(Video video) {
        em.persist(video);
    }
    
    public void markSent(Video video) {
        video.setSent(true);
        video.setCreated(new Date());
        em.merge(video);
    }

    public Video get(String uri) {
        try {
            return (Video) em.createQuery("select v from Video v where v.uri = :uri").setMaxResults(1).setParameter("uri", uri)
                            .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Video> videosToSend(int limit) {
        return em.createQuery("select v from Video v order by v.created asc").setMaxResults(limit)
                        .getResultList();
    }
    
    public Video videoToSend() {
        return (Video) em.createQuery("select v from Video v order by v.created asc").setMaxResults(1).getSingleResult();
    }
}
