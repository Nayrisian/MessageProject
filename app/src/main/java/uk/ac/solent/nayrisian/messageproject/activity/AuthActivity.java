package uk.ac.solent.nayrisian.messageproject.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.solent.nayrisian.messageproject.R;
import uk.ac.solent.nayrisian.messageproject.database.DatabaseHandler;
import uk.ac.solent.nayrisian.messageproject.database.table.Account;
import uk.ac.solent.nayrisian.messageproject.encryption.MD5;
import uk.ac.solent.nayrisian.messageproject.utility.Error;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class AuthActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    DatabaseHandler _dbHandler = new DatabaseHandler(this);
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AuthTask _authTask = null;
    private boolean _verified = false;
    private boolean _changeUsername = false;

    // UI references.
    private AutoCompleteTextView _txtEmail;
    private EditText _txtUsername;
    private EditText _txtPassword;
    private Button _btnAuth;
    private Button _btnViewAll;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        // Set up the login form.
        _txtEmail = (AutoCompleteTextView) findViewById(R.id.txtEmail);
        populateAutoComplete();

        _txtUsername = (EditText) findViewById(R.id.txtUsername);

        _txtPassword = (EditText) findViewById(R.id.txtPassword);
        _txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        _txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _btnAuth.setText(getString(R.string.action_sign_in));
                _verified = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        _txtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _btnAuth.setText(getString(R.string.action_sign_in));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        _txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _btnAuth.setText(getString(R.string.action_sign_in));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        _btnAuth = (Button) findViewById(R.id.btnAuthentication);
        _btnAuth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        _btnViewAll = (Button) findViewById(R.id.btnViewAll);
        _btnViewAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = _dbHandler.displayTable();
                if (cursor.getCount() == 0) {
                    showMessage("Error", "No data.");
                    return;
                }
                StringBuilder buffer = new StringBuilder();
                while (cursor.moveToNext()) {
                    buffer.append("ID : " + cursor.getString(0) + "\n");
                    buffer.append("Email : " + cursor.getString(1) + "\n");
                    buffer.append("User : " + cursor.getString(2) + "\n");
                    buffer.append("Pass : " + cursor.getString(3) + "\n\n");
                }
                showMessage("Data", buffer.toString());
            }
        });

        mLoginFormView = findViewById(R.id.login_auth_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
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
            Snackbar.make(_txtEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (_authTask != null) {
            return;
        }

        // Reset errors.
        _txtEmail.setError(null);
        _txtUsername.setError(null);
        _txtPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = _txtEmail.getText().toString();
        String username = _txtUsername.getText().toString();
        String password = _txtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            _txtPassword.setError(getString(R.string.error_invalid_password));
            focusView = _txtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            _txtEmail.setError(getString(R.string.error_field_required));
            focusView = _txtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            _txtEmail.setError(getString(R.string.error_invalid_email));
            focusView = _txtEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            _authTask = new AuthTask(email, username, password);
            _authTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
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
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
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

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(AuthActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        _txtEmail.setAdapter(adapter);
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
        private final String _email;
        private final String _username;
        private final String _password;

        private boolean incorrectPassword = false;

        AuthTask(String email, String username, String password) {
            _email = email;
            _username = username;
            _password = password;
        }

        private boolean authAccount(Account account) {
            String password;
            if (account.isValid()) {
                password = MD5.hash(_password);
                if (password.equals(account.getPassword())) {
                    Log.d("Auth", "Password matches.");
                    _changeUsername = !_username.equals(account.getUsername());
                    return true;
                } else {
                    Log.d("Auth", "Incorrect password.");
                    incorrectPassword = true;
                    return false;
                }
            } else {
                Log.d("Auth", "Error on authentication.");
                Error.log("Error at AuthActivity#AuthTask#doInBackground(Void...)");
                Error.display("Cannot validate account.", getBaseContext());
                return false;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: Rework different hashing algorithms into database; Storing the algorithm in a table on TABLE_ACCOUNTS.
            Account account = _dbHandler.getAccount(_email);
            long rowID;
            if (account == null) {
                // Account doesn't exist.
                Log.d("Auth", "Account doesn't exist.");
                if (_verified) {
                    // Create account.
                    Log.d("Auth", "Creating account.");
                    rowID = _dbHandler.addAccount(_email, _username, _password);
                    if (rowID == -1) {
                        // Account creation failed.
                        Log.d("Auth", "Account creation failed.");
                        Error.log("Error at DatabaseHandler#addAccount(String, String, String)");
                        Error.display("Cannot create account.", getBaseContext());
                        return false;
                    }
                    // Find account.
                    Log.d("Auth", "Finding account.");
                    account = _dbHandler.getAccount(_email);
                    if (account == null) {
                        Error.log("Error at DatabaseHandler#getAccount(String)");
                        account = _dbHandler.getAccount(rowID);
                        if (account == null) {
                            // Account not found.
                            Log.d("Auth", "Account not found.");
                            Error.log("Error at DatabaseHandler#getAccount(long)");
                            Error.display("Cannot find account.", getBaseContext());
                            return false;
                        }
                    }
                    // Account found, authenticate.
                    Log.d("Auth", "Account found, authenticate.");
                } else {
                    // Waiting for user verification.
                    Log.d("Auth", "Waiting for user verification.");
                    return false;
                }
            }
            return authAccount(account);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            _authTask = null;
            showProgress(false);

            if (success) {
                // TODO: Ask how to finish activity in this scenario.
                if (_changeUsername) {
                    // TODO: Change MainActivity to ChangeUsername activity.
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (!_verified) {
                _btnAuth.setText(getString(R.string.action_register));
                _verified = true;
            } else if (incorrectPassword) {
                _txtPassword.setError(getString(R.string.error_incorrect_password));
                _txtPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            _authTask = null;
            showProgress(false);
        }
    }
}