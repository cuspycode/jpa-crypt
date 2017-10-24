<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.cuspycode.jpacrypt.ContextListener" %>
<%@ page import="com.cuspycode.jpacrypt.Secrets" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="javax.persistence.EntityTransaction" %>
<%
	EntityManager em = ContextListener.createEntityManager();

	// Add a secret

	String secret = request.getParameter("secret");
	if (secret == null) {
		secret = "Foo bar";
	}

	Secrets s = new Secrets();
	s.setSecret(secret);
	EntityTransaction tx = em.getTransaction();
	tx.begin();
	em.persist(s);
	tx.commit();
%>
<html>
<head><title>JPA-Crypt Example</title></head>
<body>
<h1>JPA-Crypt Example</h1>

<%
	for (int i=1; i<10; i++) {
	    Secrets x = em.find(Secrets.class, (long) i);
	    if (x == null) break;
%>
<p>
<%= x.getId() %>, <%= x.getSecret() %>, <%= x.getEncryptedSecret() %>
</p>
<%
	}
%>
</body>
</html>
