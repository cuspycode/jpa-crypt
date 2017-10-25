# jpa-crypt

Store and retrieve encrypted data in SQL databases using JPA.

JPA 2.1 supports automatic table creation and generating DDL statements, so no actual SQL coding is required.

The encryption algorithm used is AES-GCM, which is a shorthand for the mouthful "Advanced Encryption Standard -- Galois/Counter Mode",
but this can easily be changed by changing a few parameters in the source code.

## How the crypto works

All the cryptography code is contained in the file "Crypto.java". The central method in that class is "setup",
which has three arguments: "mode", "iv", and "password". The "mode" is either 1 (ENCRYPT_MODE), or 2 (DECRYPT_MODE).

The "iv" is the Initialization Vector, which is a small set of random bits that are used to initialize the state
of the cipher algorithm before it starts encrypting the plaintext. The Initialization Vector must be different
for each encrypted message, otherwise the encryption may be significantly weakened or broken whenever two different
plaintext messages share a common prefix. This requirement is fulfilled by having "setup" generate a new random
Initialization Vector each time a new message is encrypted.

The "password" is a string that is provided by the application. It is used to derive the encryption key,
which needs to be exactly 128 bits long. This is accomplished by taking the SHA256 hash of the password,
and then using the first 128 bits as the key. Of course the application could provide a 128-bit key directly,
but by using this method the application can provide a password of arbitrary length, which is more convenient
and also makes it easier to change from AES-GCM to an algorithm that uses a different key length. In this
example, the application reads the password from a properties file. This properties file should have restricted
permissions, and the password should have enough entropy to protect against any anticipated threats. But since
the derived key is 128 bits, there is no point in having more entropy than that. 128 bits corresponds to about
22 random alphanumeric characters.

The calls to the encryption and decryption methods happen in the entity class "Secrets". A special getter and
setter method marked "@Transient" perform encryption and decryption respectively, before calling the entity
getter and setter. The annotation "@Transient" stops JPA from trying to map the getter and setter pair to an
SQL column.
