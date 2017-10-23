# jpa-crypt

Store and retrieve encrypted data in SQL databases using JPA.

JPA 2.1 supports automatic table creation and generating DDL statements, so no actual SQL coding is required.

The encryption algorithm used is AES-GCM, which is a shorthand for the mouthful "Advanced Encryption Standard -- Galois/Counter Mode",
but this can easily be changed by changing a few parameters in the source code.

