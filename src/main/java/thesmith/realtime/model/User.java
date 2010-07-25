package thesmith.realtime.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

import com.google.appengine.api.datastore.Key;

@Entity
@Table(name = "user")
public @Data class User implements Serializable, Cloneable {
  private static final long serialVersionUID = -1071691679726356029L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key id;
  
  @Version
  @Column(name = "version")
  protected long version;

  @Basic
  @Column(name = "gid", length = 255)
  protected String gid;
  
}
