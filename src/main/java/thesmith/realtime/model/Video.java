package thesmith.realtime.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Data;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@Entity
@Table(name = "video")
public @Data class Video implements Serializable, Cloneable {
  private static final long serialVersionUID = -1071691679726356019L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key id;
  
  @Version
  @Column(name = "version")
  protected long version;

  @Basic
  @Column(name = "uri", length = 255)
  protected String uri;

  @Basic
  @Column(name = "title", length = 255)
  protected String title;

  @Basic
  @Column(name = "desc", length = 511)
  protected Text desc;
  
  @Basic
  @Column(name = "image", length = 255)
  protected String image;
  
  @Basic
  @Column(name = "link", length = 255)
  protected String link;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created")
  private Date created;
  
  @Basic
  @Column(name = "sent")
  private Boolean sent;
  
  public void setDesc(String desc) {
      this.desc = new Text(desc);
  }
  
  public String getDesc() {
      return this.desc.getValue();
  }
  
  public String toString() {
      return "*"+title+"*\n"+desc.getValue()+"\nWatch it here: "+link+"\n\n";
  }
  
  public String toXML() {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><video><uri>"+uri+"</uri><title>"+title+"</title><desc>"+desc.getValue()+"</desc><image>"+image+"</image><link>"+link+"</link></video>";
  }
  
  public String toJson() {
      String json = "{\"uri\":\""+enc(uri)+"\",\"title\":\""+enc(title)+"\",\"desc\":\""+enc(desc.getValue())+"\",\"image\":\""+enc(image)+"\",\"link\":\""+enc(link)+"\"}";
      try {
        return new JSONObject(json).toString();
    } catch (JSONException e) {
        json = "{\"uri\":\""+enc(uri)+"\",\"title\":\""+enc(title)+"\",\"image\":\""+enc(image)+"\",\"link\":\""+enc(link)+"\"}";
        return json;
    }
  }
  
  private String enc(String in) {
      return in.replace("\"", "").replace("'", "");
  }
}
