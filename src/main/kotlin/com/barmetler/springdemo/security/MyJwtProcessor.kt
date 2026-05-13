package com.barmetler.springdemo.security

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.crypto.Ed25519Verifier
import com.nimbusds.jose.jwk.JWKMatcher
import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.OctetKeyPair
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.JOSEObjectTypeVerifier
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier

class MyJwtProcessor<C : SecurityContext>(private val jwkSource: JWKSource<C>) : DefaultJWTProcessor<C>() {
    init {
        jwsTypeVerifier = JOSEObjectTypeVerifier { _, _ -> }
        jwsKeySelector = jwsKeySelector(jwkSource)
        jwtClaimsSetVerifier = JWTClaimsSetVerifier { _, _ -> }
    }

    @Throws(BadJOSEException::class, JOSEException::class)
    override fun process(signedJWT: SignedJWT, context: C?): JWTClaimsSet? {
        if (signedJWT.header?.algorithm === JWSAlgorithm.EdDSA) {
            val claimsSet = extractJWTClaimsSet(signedJWT)
            val keyCandidates = jwkSource.get(
                JWKSelector(
                    JWKMatcher.Builder()
                        .algorithm(signedJWT.header.algorithm)
                        .let { if (signedJWT.header.keyID != null) it.keyID(signedJWT.header.keyID) else it }
                        .build(),
                ),
                context,
            ).filterIsInstance<OctetKeyPair>()

            for (key in keyCandidates) {
                val verifier = Ed25519Verifier(key)

                val validSignature = signedJWT.verify(verifier)

                if (validSignature) {
                    return verifyJWTClaimsSet(claimsSet, context)
                }
            }
        }
        return super.process(signedJWT, context)
    }

    private fun <C : SecurityContext> jwsKeySelector(
        jwkSource: JWKSource<C>,
    ): JWSKeySelector<C?> = JWSVerificationKeySelector<C>(
        JWSAlgorithm.Family.SIGNATURE.toSet(),
        jwkSource,
    )
}
