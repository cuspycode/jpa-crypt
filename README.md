# jpa-crypt

Store and retrieve encrypted data in SQL databases using JPA.

JPA 2.1 supports automatic table creation and generating DDL statements, so no actual SQL coding is required.

The encryption algorithm used is AES-GCM, which is a shorthand for the mouthful _Advanced Encryption Standard -- Galois/Counter Mode_,
but this can easily be changed by changing a few parameters in the source code.

## How the crypto works

All the cryptography code is contained in the file `Crypto.java`. The central method in that class is `setup`,
which has three arguments: `mode`, `iv`, and `password`. The `mode` argument is either 1 (`ENCRYPT_MODE`),
or 2 (`DECRYPT_MODE`).

The `iv` argument is the Initialization Vector, which is a small set of random bits that are used to initialize the
state of the cipher algorithm before it starts encrypting the plaintext. The Initialization Vector must be different
for each encrypted message, otherwise the encryption may be significantly weakened or broken whenever two different
plaintext messages share a common prefix. This requirement is fulfilled by having `setup` generate a new random
Initialization Vector each time a new message is encrypted.

The `password` argument is a string that is provided by the application. It is used to derive the encryption
key, which needs to be exactly 128 bits long. This is accomplished by taking the SHA256 hash of the password,
and then using the first 128 bits as the key. Of course the application could provide a 128-bit key directly,
but by using this method the application can provide a password of arbitrary length, which is more convenient
and also makes it easier to change from AES-GCM to an algorithm that uses a different key length. In this
example, the application reads the password from a properties file. This properties file should have restricted
permissions, and the password should have enough entropy to protect against any anticipated threats. But since
the derived key is 128 bits (or slightly less, since there is a non-zero probability of hash collisions), there
is no point in having more entropy than that in the password. 128 bits corresponds to about 22 random alphanumeric
characters.

## Reading and writing to the database

The calls to the encryption and decryption methods happen in the entity class `Secrets.java`. A special getter
and setter method marked `@Transient` perform encryption and decryption respectively, before calling the entity
getter and setter. The annotation `@Transient` stops JPA from trying to map the getter and setter pair to an
SQL column.

The encrypted data is stored in the SQL column named `SECRET` as `{IV}:{ENCRYPTED}`, where `{ENCRYPTED}` and
`{IV}` are the Base64 encodings of the encrypted message and its Initialization Vector, respectively. 

The default SQL type for the column is `VARCHAR`, but this can be changed to a "large object" character type
by adding the `@Lob` annotation to the getter and setter pair.

## The master password

The master password is stored in `WEB-INF/config.properties`. This is just for convenience when trying it out.
The file path can be reconfigured by editing `ContextListener.java`.

## Security note

This example code uses H2 version 1.4.196 as the database. That H2 version is vulnerable to CVE-2021-42392 if the H2 console servlet is deployed and made available without security constraints to random dudes on the Internet. So don't do that.
