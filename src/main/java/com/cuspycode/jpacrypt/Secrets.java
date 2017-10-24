package com.cuspycode.jpacrypt;

import javax.persistence.*;
import java.io.Serializable;

import static com.cuspycode.jpacrypt.Crypto.encrypt;
import static com.cuspycode.jpacrypt.Crypto.decrypt;

@Entity
@Table(name = "SECRETS")
public class Secrets implements Serializable {
    private long id;
    private String secret;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() { return id; }
    public void setId(long x) { id = x; }

    @Column(name = "SECRET")
    public String getEncryptedSecret() { return secret; }
    public void setEncryptedSecret(String x) { secret = x; }

    @Transient
    public String getSecret() throws Exception {
	String password = ContextListener.getConfig("jpacrypt.password");
	return decrypt(getEncryptedSecret(), password);
    }
    public void setSecret(String x) throws Exception {
	String password = ContextListener.getConfig("jpacrypt.password");
	setEncryptedSecret(encrypt(x, password));
    }
}

