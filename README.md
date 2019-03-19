# ParrotTalk

INSTALL

To install ParrotTalk in Java, first clone the ASN1 project, then the ParrotTalk project. Link the ASN1 project into ParrotTalk and run the ParrotTalk tests.


OVERVIEW

ParrotTalk is an encrypted connection framework. Currently allowing anonymous 2048-bit key negotiation to establish user-provided encryption cipher and user-provided encoding and decoding, both through a provided SessionAgentMap to a starting SessionAgent server. Please look in the test case ThunkHelloWorldTest for building these maps and running a connection iwth data passing after encryption is established. There is a 4-way negotiation, from ProtocolOffered/Accepted to Go/GoToo. In using RSA 2048 signature validation and DH 2048 primes to establish the key used within the selected Cipher. The Cipher and Encoder are selected by name through the negotiation protocol. Currently three Ciphers are selectable, AESede, DESede, and DES. There are two encoders tested, asn1der, and Bytes. This protocol is described here, in these documents.

Here is the slides describing the version 3.7 protocol:
https://github.com/CallistoHouseLtd/ParrotTalk/blob/master/docs/ParrotTalkFrameDesign-3.7.pdf

Here is the previous yet still supported, version 3.6 protocol:
https://github.com/CallistoHouseLtd/ParrotTalk/blob/master/docs/ParrotTalkFrameDesign-3.6.pdf

and an IETF draft document, specific to version 3.6

https://github.com/CallistoHouseLtd/ParrotTalk/blob/master/docs/draft-withers-parrot-talk-v36-00.pdf

Here is a diagram of the protocol stack

![Protocol Stack](https://github.com/CallistoHouseLtd/ParrotTalk/blob/master/docs/a%20Transceiver.jpeg)

For as to use cases, this encrypted connection has no third party, man-in-the-middle situation by not using Certificates. As such, this is a tight implementation of NSA-proof encryption without explicit authorization beyond knowledge of a host:port. The use cases involve any communication desired to be encrypted with such high encryption. The support will last my lifetime, so we have a settled solution, here in the third version, provided here. It requires version 115 of Cryptography, as a prerequisite. Both run on Squeak and Pharo.

http://www.squeaksource.com/Cryptography/Cryptography-rww.115.mcz

http://www.squeaksource.com/Cryptography/ParrotTalk-rww.25.mcz
For Java, currently supporting version 3.6 only yet still able to connect to a Squeak/Pharo agent, clone these repositories:

https://github.com/CallistoHouseLtd/ASN1

https://github.com/CallistoHouseLtd/ParrotTalk

The current use is with my Raven system, a promise-based distributed object implementation. I am working to bring ParrotTalk to Java and allow Raven to operate interdependently between Squeak, Pharo, Java and any other languages which can support ParrotTalk and STON. My latest efforts with Raven are to bring STON as the Layer 6 encoding. 

http://www.squeaksource.com/Oceanside/Ston-Core-SvenVanCaekenberghe.36.mcz

http://www.squeaksource.com/Oceanside/STON-Text%20support-TheIntegrator.2.mcz

http://www.squeaksource.com/Oceanside/Ston-Tests-SvenVanCaekenberghe.34.mcz

http://www.squeaksource.com/Cryptography/Raven-rww.25.mcz

Here is a log of the 2-vat test in Java:

https://gist.github.com/RobWithers/2b428ff541bfdc9d85699c8c1729f34c

I designed the next version of my ParrotTalk negotiation protocol, version 3,7 a 5 message exchange to build a 256-bit AESede connection, with an user specified encoding. This is an advance from the 8 messaging version 3.6. I took inspiration from the new TLS 1.3 message exchange, which is a 3 message exchange.

I decided to keep the 2 message protocol negotiation messages, ProtocolOffered and ProtocolAccepted. This allows one to specify in the SessionAgentMap, the versions supported, v3.6 and v3.7, as well as the preferred protocol version to use.

Well, instead of getting the 3-vat tests working, I decided to dive into the protocol. The current ParrotTalk protocol is ParrotTalk-3.6 and is an 8-way msg exchange. Gleaning from the new TLS 1.3 I decided to advance the ParrotTalk-v3.7 protocol which is a 5-way handshake.

The first two msgs (ProtocolOffered/ProtocolAccepted) are negotiating which protocol version to use, so I added a class SessionProtocolSelector which handles these two methods. I have the option of negotiating ParrotTalk-3.6 or ParrotTalk-3.7. Based upon which protocol version is negotiated I manipulate the stack to pop the ProtocolSelector and push the SessionOperations for each protocol version. I split the stateMap to handle the Protocol messages in the Selector and the handshake traffic for each version in the specified SessionOperations. v3.6 uses the current SessionOperations minus the protocol negotiation states which moved over to the selector.

The new SessionOperaations_v3_7 will have an updated stateMap to handle the 3-way msg exchange (Hello_v3_7, Response_v3_7 and Signature_v3_7). Thus after I add the protocol version to the AgentMap, the SessionAgent will install the Selector with the specified protocol version and when 3.7 or 3.6 is negotiated the stack will be thunked with the correct SessionOperations and the SecurityOps from the selector (to capture the local and remote traffic for signatures) will be installed in the protocol SessionOperations. Since the SessionOperations holds the stateMap and all of the process and send methods for the version of the protocol, the only difference between the two protocol versions is the specific SessionOperations.

The SecurityOps and all other thunks are the same between each version. That's freaking cool, I think. That just that one Operations thunk is the difference between versions. If I decide to implement SSL on the ThunKStack, the other change will need to be the ReceivingFrameBuffer, since the SSL frames are fundamentally different from ParrotTalk's frames. Ditto with SSH. Anyways, I needed to share my thoughts on the changes I am making. Thanks for your consideration! Thanks for reading my ramblings!

It works in Squeak (https://squeak.org/) and Pharo (http://pharo.org/). In any image, load these packages, please

http://www.squeaksource.com/Cryptography/Cryptography-rww.115.mcz

http://www.squeaksource.com/Cryptography/ParrotTalk-rww.25.mcz

Then check out the ParrotTalk tests.

For Java, currently supporting version 3.6 only yet still able to connect to a Squeak/Pharo agent, clone these repositories:

https://github.com/CallistoHouseLtd/ASN1

https://github.com/CallistoHouseLtd/ParrotTalk
