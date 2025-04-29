package pro.branium.messenger.data.local.datasource

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.branium.messenger.domain.repository.TokenStorage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

@Singleton // Make it a singleton if using Hilt/Dagger
class TokenStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context // Inject application context
) : TokenStorage {
    private val accessTokenFile = "access_token.enc"
    private val refreshTokenFile = "refresh_token.enc"
    private val keyAlias = "token_storage_key"

    override suspend fun saveAccessToken(token: String) {
        encryptAndSaveToFile(token, accessTokenFile)
    }

    override suspend fun saveRefreshToken(token: String) {
        encryptAndSaveToFile(token, refreshTokenFile)
    }

    override suspend fun getAccessToken(): String? {
        return decryptFromFile(accessTokenFile)
    }

    override suspend fun getRefreshToken(): String? {
        return decryptFromFile(refreshTokenFile)
    }

    override suspend fun clearTokens() {
        withContext(Dispatchers.IO) {
            File(context.filesDir, accessTokenFile).delete()
            File(context.filesDir, refreshTokenFile).delete()
        }
    }

    private suspend fun encryptAndSaveToFile(plainText: String, fileName: String) {
        withContext(Dispatchers.IO) {
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(iv.size)
                outputStream.write(iv)
                CipherOutputStream(outputStream, cipher).use { cipherOut ->
                    cipherOut.write(plainText.toByteArray(Charsets.UTF_8))
                }
            }
        }
    }

    private suspend fun decryptFromFile(fileName: String): String? {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@withContext null

            val secretKey = getOrCreateSecretKey()
            FileInputStream(file).use { inputStream ->
                val ivSize = inputStream.read()
                val iv = ByteArray(ivSize)
                inputStream.read(iv)

                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                val spec = GCMParameterSpec(128, iv)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

                CipherInputStream(inputStream, cipher).use { cipherIn ->
                    return@withContext cipherIn.readBytes().toString(Charsets.UTF_8)
                }
            }
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (keyStore.containsAlias(keyAlias)) {
            return (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return keyGenerator.generateKey()
    }
}