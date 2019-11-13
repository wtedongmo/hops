package com.afsoltech.core.repository.utils


class ProviderEncryptDecrypt {

    object Passphrase{
        fun getPass(provider: ProviderEntity) : String {
            val passPhrase = provider.outboundUserPassphrase + provider.providerId +
                    provider.outboundUserId + provider.participantName
            return passPhrase
        }
    }

    object Encryption{
        fun encrypt(provider: ProviderEntity, password: String) : String{
            val passPhrase = Passphrase.getPass(provider)
            return EncryptDecrypt.encrypt(password, passPhrase)
        }
    }

    object Decryption{
        fun decrypt(provider: ProviderEntity) : String{
            provider.outboundUserPassword?.let {
                val passPhrase = Passphrase.getPass(provider)
                return EncryptDecrypt.decrypt(provider.outboundUserPassword, passPhrase)
            }
            return ""
        }
    }
}