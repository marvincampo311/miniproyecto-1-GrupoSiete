    package com.example.miiproyecto1.data.auth

    import android.util.Log
    import com.google.firebase.auth.FirebaseAuth
    import kotlinx.coroutines.tasks.await

    class AuthRepository(
        private val firebaseAuth: FirebaseAuth
    ) {
        suspend fun login(email: String, password: String): Result<Unit> {
            return try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        suspend fun register(email: String, password: String): Result<Unit> {
            return try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        fun logout() {
            Log.d("AuthTest", "Antes de signOut, currentUser=${firebaseAuth.currentUser}")
            firebaseAuth.signOut()
            Log.d("AuthTest", "Después de signOut, currentUser=${firebaseAuth.currentUser}")
        }


        fun isLoggedIn(): Boolean {
            val loggedIn = firebaseAuth.currentUser != null
            Log.d("AuthTest", "isLoggedIn() = $loggedIn, currentUser=${firebaseAuth.currentUser}")
            return loggedIn
        }
    }
