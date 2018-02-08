package com.nayrisian.dev.messageproject.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nayrisian.dev.messageproject.R;
import com.nayrisian.dev.messageproject.Setting;
import com.nayrisian.dev.messageproject.database.DatabaseHelper;
import com.nayrisian.dev.messageproject.database.type.Account;
import com.nayrisian.dev.messageproject.encryption.Hash;
import com.nayrisian.dev.messageproject.encryption.Hashtype;
import com.nayrisian.dev.messageproject.utility.Error;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class AuthActivity extends BaseActivity implements LoaderCallbacks<Cursor> {
    private static final int REQUEST_READ_CONTACTS = 0;

    // Database reference.
    private DatabaseHelper mDBHelper;
    // Application state references.
    private AuthTask mAuthTask = null;
    private State mActionState = State.LOGIN;
    private boolean mChangeUsername = false;
    // UI references.
    private AutoCompleteTextView mTxtEmail;
    private EditText mTxtUsername;
    private EditText mTxtPassword;
    private Button mBtnAuth;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        // UI references.
        mLoginFormView = findViewById(R.id.login_auth_form);
        mProgressView = findViewById(R.id.login_progress);
        mTxtEmail = (AutoCompleteTextView) findViewById(R.id.txtEmail);
        mTxtPassword = (EditText) findViewById(R.id.txtPassword);
        mTxtUsername = (EditText) findViewById(R.id.txtUsername);
        mBtnAuth = (Button) findViewById(R.id.btnAuthentication);
        Switch switchTheme = (Switch) findViewById(R.id.switchTheme);
        // Link database.
        mDBHelper = DatabaseHelper.get(this);
        // Setup UI.
        mTxtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.email || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mTxtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.password || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mTxtUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.username || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        populateAutoComplete();
        // Input text resetting - Resets the state of logic or register.
        mTxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtnAuth.setText(getString(R.string.action_sign_in));
                mActionState = State.LOGIN;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTxtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtnAuth.setText(getString(R.string.action_sign_in));
                mActionState = State.LOGIN;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Authenticate button assignment.
        mBtnAuth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        // UI has been created.
        switchTheme.setChecked(Setting.getStyle() == Setting.Style.DARK);
        switchTheme.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.toggleStyle();
                Toast.makeText(AuthActivity.this, "Style: " + Setting.getStyle().toString(), Toast.LENGTH_SHORT).show();
                AuthActivity.this.recreate();
            }
        });
    }

    /**
     * Attempts to sign in or register the mAccount specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
        String email = mTxtEmail.getText().toString().toLowerCase();
        String username = mTxtUsername.getText().toString();
        String password = mTxtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Reset errors.
        mTxtEmail.setError(null);
        mTxtUsername.setError(null);
        mTxtPassword.setError(null);

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mTxtEmail.setError(getString(R.string.error_field_required));
            focusView = mTxtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mTxtEmail.setError(getString(R.string.error_invalid_email));
            focusView = mTxtEmail;
            cancel = true;
        } else if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mTxtPassword.setError(getString(R.string.error_invalid_password));
            focusView = mTxtPassword;
            cancel = true;
        } else if (mActionState == State.REGISTER) {
            if (TextUtils.isEmpty(username) || !isUsernameValid(username)) {
                mTxtUsername.setError(getString(R.string.error_invalid_username));
                focusView = mTxtUsername;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new AuthTask(email, username, password, Hashtype.SHA1);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches("([a-z0-9_]+@[a-z0-9]+(\\.[a-z]+)*)");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 2;
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mTxtEmail, R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS},
                                    REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(AuthActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mTxtEmail.setAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private enum State {
        REGISTER, LOGIN
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        //int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AuthTask extends AsyncTask<Void, Void, Boolean> {
        private String mEmail;
        private String mUsername;
        private String mPassword;
        private Hashtype mHashtype;
        private Account mAccount;

        private boolean mInvalidPassword = false;

        AuthTask(String email, String username, String password, Hashtype hashtype) {
            mEmail = email;
            mUsername = username;
            mPassword = password;
            mHashtype = hashtype;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mAccount = mDBHelper.getAccount(AuthActivity.this, mEmail, 1);
            long rowID;
            if (mAccount == null) {
                // Account doesn't exist.
                if (mActionState == State.LOGIN) {
                    // Waiting for user verification.
                    return false;
                } else if (mActionState == State.REGISTER) {
                    // Create mAccount.
                    rowID = mDBHelper.addAccount(AuthActivity.this, mEmail, mUsername, mPassword, mHashtype);
                    if (rowID == -1) {
                        // Account creation failed.
                        Error.log(new Exception("Error at DatabaseTest#addAccount(String, String, String)"), AuthActivity.this);
                        return false;
                    }
                    // Find mAccount for verification.
                    mAccount = mDBHelper.getAccount(AuthActivity.this, mEmail, 1);
                    if (mAccount == null) {
                        // Account not found with email.
                        Error.log(new Exception("Error at DatabaseTest#getmAccount(String)"), AuthActivity.this);
                        mAccount = mDBHelper.getAccount(AuthActivity.this, rowID, 1);
                        if (mAccount == null) {
                            // Account not found with ID.
                            Error.log(new Exception("Error at DatabaseTest#getmAccount(long)"), AuthActivity.this);
                            return false;
                        }
                    }
                }
            }
            String password;
            if (mAccount != null) {
                if (mAccount.isValid()) {
                    password = Hash.hash(mPassword, mHashtype);
                    if (password.equals(mAccount.getPassword())) {
                        mChangeUsername = !mUsername.equals(mAccount.getUsername());
                        return true;
                    } else {
                        mInvalidPassword = true;
                        return false;
                    }
                } else {
                    Error.log(new Exception("Error at AuthActivity#AuthTask#doInBackground(Void...)"), AuthActivity.this);
                    return false;
                }
            } else
                return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Setting.setAccount(mAccount);
                if (mChangeUsername) {
                    // TODO: Change MainActivity to ChangeUsername activity.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (mActionState == State.LOGIN) {
                mBtnAuth.setText(getString(R.string.action_register));
                mActionState = State.REGISTER;
            } else if (mInvalidPassword) {
                mTxtPassword.setError(getString(R.string.error_incorrect_password));
                mTxtPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}