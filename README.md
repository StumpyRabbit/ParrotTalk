# ParrotTalk

INSTALL

To install ParrotTalk in Java, first clone the ASN1 project, then the ParrotTalk project. Link the ASN1 project into ParrotTalk and run the ParrotTalk tests.

OVERVIEW

ParrotTalk is an encrypted connection framework. Currently allowing anonymous 2048-bit key negotiation to establish user-provided encryption cipher and user-provided encoding and decoding, both through a provided SessionAgentMap to a starting SessionAgent server. Please look in the test case ThunkHelloWorldTest for building these maps and running a connection iwth data passing after encryption is established. There is a 4-way negotiation, from ProtocolOffered/Accepted to Go/GoToo. In using RSA 2048 signature validation and DH 2048 primes to establish the key used within the selected Cipher. The Cipher and Encoder are selected by name through the negotiation protocol. Currently three Ciphers are selectable, AESede, DESede, and DES. There are two encoders tested, asn1der, and Bytes. This protocol is described here, in this document.

https://github.com/CallistoHouseLtd/ParrotTalk/blob/master/docs/ParrotTalkFrameDesign-3.6.pdf

and an IETF draft document

https://github.com/CallistoHouseLtd/ParrotTalk/blob/master/docs/draft-withers-parrot-talk-v36-00.pdf

Here is a diagram of the protocol stack

![Protocol Stack](https://github.com/CallistoHouseLtd/ParrotTalk/blob/master/docs/a%20Transceiver.jpeg)

Here is a log of the 2-vat test in Java:

![](https://gist.github.com/RobWithers/2b428ff541bfdc9d85699c8c1729f34c)

For as to use cases, this encrypted connection has no third party, man-in-the-middle situation by not using Certificates. As such, this is a tight implementation of NSA-proof encryption without explicit authorization beyond knowledge of a host:port. The use cases involve any communication desired to be encrypted with such high encryption. The support will last my lifetime, so we have a settled solution, here in the third version, provided here. It requires version 115 of Cryptography, as a prerequisite. Both run on Squeak and Pharo.

http://www.squeaksource.com/Cryptography/Cryptography-rww.115.mcz

http://www.squeaksource.com/Cryptography/ParrotTalk-rww.22.mcz

The current use is with my Raven system, a promise-based distributed object implementation. I am working to bring ParrotTalk to Java and allow Raven to operate interdependently between Squeak, Pharo, Java and any other languages which can support ParrotTalk and STON. My latest efforts with Raven are to bring STON as the Layer 6 encoding. 

http://www.squeaksource.com/Oceanside/Ston-Core-SvenVanCaekenberghe.36.mcz

http://www.squeaksource.com/Oceanside/STON-Text%20support-TheIntegrator.2.mcz

http://www.squeaksource.com/Oceanside/Ston-Tests-SvenVanCaekenberghe.34.mcz

http://www.squeaksource.com/Cryptography/Raven-rww.24.mcz
