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

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() { return id; }
    public void setId(long x) { id = x; }

    @Column(name = "SECRET")
    public String getEncryptedSecret() { return secret; }
    public void setEncryptedSecret(String x) { secret = x; }

    @Transient
    public String getSecret() throws Exception { return decrypt(getEncryptedSecret(), "swordfish"); }
    public void setSecret(String x) throws Exception { setEncryptedSecret(encrypt(x, "swordfish")); }

}

