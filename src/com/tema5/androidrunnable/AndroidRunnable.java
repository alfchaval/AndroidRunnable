package com.tema5.androidrunnable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AndroidRunnable extends Activity {
	private Button btnSinHilos;
	private Button btnConHilos;
	private Button btnAsync;
	private Button btnAsyncProgress;
	private ProgressBar pbarProgreso;
	private ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.android_runnable);

		pbarProgreso = (ProgressBar) findViewById(R.id.pbarProgreso);

		btnSinHilos = (Button) findViewById(R.id.btnSinHilos);
		btnSinHilos.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				pbarProgreso.setMax(100);
				pbarProgreso.setProgress(0);
				for (int i = 1; i <= 10; i++) {
					tareaLarga();
					pbarProgreso.incrementProgressBy(10);
				}

				Toast.makeText(AndroidRunnable.this, "Tarea finalizada!",
						Toast.LENGTH_SHORT).show();
			}
		});
		btnConHilos = (Button) findViewById(R.id.btnConHilos);
		btnConHilos.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						pbarProgreso.post(new Runnable() {
							public void run() {
								pbarProgreso.setProgress(0);
							}
						});
						for (int i = 1; i <= 10; i++) {
							tareaLarga();
							pbarProgreso.post(new Runnable() {
								public void run() {
									pbarProgreso.incrementProgressBy(10);
								}
							});
						}
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(AndroidRunnable.this,
										"Tarea finalizada!", Toast.LENGTH_SHORT)
										.show();
							}
						});
					}
				}).start();

			}
		});
		btnAsync = (Button) findViewById(R.id.btnAsync);
		btnAsync.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AsyncActivity asynActivity = new AsyncActivity();
				asynActivity.execute();
			}
		});

		btnAsyncProgress = (Button) findViewById(R.id.btnAsyncProgress);
		btnAsyncProgress.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				pDialog = new ProgressDialog(AndroidRunnable.this);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.setMessage("Procesando...");
				pDialog.setCancelable(true);
				pDialog.setMax(100);
				AsyncDialogActivity asynDialog = new AsyncDialogActivity();
				asynDialog.execute();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.android_runnable, menu);
		return true;
	}

	private void tareaLarga() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Clase que actualiza la barra de progreso que está en la vista
	 * 
	 * @author lourdes
	 * 
	 */
	private class AsyncActivity extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			for (int i = 1; i <= 10; i++) {
				tareaLarga();

				publishProgress(i * 10);

				if (isCancelled())
					break;
			}

			return true;
		}

		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();

			pbarProgreso.setProgress(progreso);
		}

		protected void onPreExecute() {
			pbarProgreso.setMax(100);
			pbarProgreso.setProgress(0);
		}

		protected void onPostExecute(Boolean result) {
			if (result)
				Toast.makeText(AndroidRunnable.this, "Tarea finalizada!",
						Toast.LENGTH_SHORT).show();
		}

		protected void onCancelled() {
			Toast.makeText(AndroidRunnable.this, "Tarea cancelada!",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Clase que contiene una barra de progreso
	 * 
	 * @author lourdes
	 * 
	 */
	private class AsyncDialogActivity extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			for (int i = 1; i <= 10; i++) {
				tareaLarga();
				publishProgress(i * 10);
				if (isCancelled())
					break;
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
			pDialog.setProgress(progreso);
		}

		@Override
		protected void onPreExecute() {
			pDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					AsyncDialogActivity.this.cancel(true);
				}
			});
			pDialog.setProgress(0);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				pDialog.dismiss();
				Toast.makeText(AndroidRunnable.this, "Tarea finalizada!",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(AndroidRunnable.this, "Tarea cancelada!",
					Toast.LENGTH_SHORT).show();
		}
	}

}
