package pro.branium.messenger.presentation

import android.content.IntentSender
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pro.branium.messenger.presentation.navigation.AppNavigation
import pro.branium.messenger.presentation.theme.Messenger2025Theme
import pro.branium.messenger.presentation.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var oneTapClient: SignInClient
    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("your-web-client-id") // Replace with your OAuth 2.0 Client ID
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                authViewModel.onGoogleSignInResult(idToken, {
                    // Handle success (e.g., navigate in Navigation)
                }, { errorMessage ->
                    // Handle error (e.g., show toast)
                })
            } catch (e: ApiException) {
                authViewModel.onGoogleSignInResult(null, {}, { "Sign-in failed: ${e.message}" })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        oneTapClient = Identity.getSignInClient(this)

        lifecycleScope.launch {
            authViewModel.startGoogleSignIn.collectLatest { shouldStart ->
                if (shouldStart) {
                    launchGoogleSignIn()
                    authViewModel.onGoogleSignInStarted()
                }
            }
        }

        setContent {
            Messenger2025Theme {
                AppNavigation()
            }
        }
    }

    private fun launchGoogleSignIn() {
        authViewModel.startGoogleSignIn() // Signal start (optional)
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    signInLauncher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    // Handle error
                }
            }
            .addOnFailureListener { e ->
                authViewModel.onGoogleSignInResult(null, {}, { "Failure: ${e.message}" })
            }
    }
}
