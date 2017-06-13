package zorail.rohan.com.authentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener{

    lateinit private var mAuth:FirebaseAuth
    lateinit var mGoogleApiClient:GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_id))
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build()
        mAuth = FirebaseAuth.getInstance()
        sign_in_button.setOnClickListener { singIn() }
        sign_out_button.setOnClickListener { signOut() }

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this,"Google Play Services Error"+p0.errorMessage,Toast.LENGTH_SHORT).show();
    }

    fun singIn(){
        val intent:Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(intent,9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 9001){
            val result:GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                val account:GoogleSignInAccount = result.signInAccount!!
                fireBaseAuthWithGoogle(account)
            }
        }
    }
    fun fireBaseAuthWithGoogle(acct:GoogleSignInAccount){
        val credential:AuthCredential = GoogleAuthProvider.getCredential(acct.idToken,null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { p0 ->
                    if(p0.isSuccessful){
                        val user:FirebaseUser = mAuth.currentUser!!
                        Toast.makeText(applicationContext,user.displayName,Toast.LENGTH_LONG).show()
                    }
                }
    }
    fun signOut(){
        mAuth.signOut()
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            Toast.makeText(applicationContext,"Singed Out",Toast.LENGTH_SHORT).show() }
    }
}