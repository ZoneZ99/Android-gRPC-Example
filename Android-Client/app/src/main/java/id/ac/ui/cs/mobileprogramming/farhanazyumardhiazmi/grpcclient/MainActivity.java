package id.ac.ui.cs.mobileprogramming.farhanazyumardhiazmi.grpcclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import id.ac.ui.cs.mobileprogramming.farhanazyumardhiazmi.grpcclient.databinding.ActivityMainBinding;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.ExampleGrpc;
import io.grpc.examples.ExampleReply;
import io.grpc.examples.ExampleRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = ActivityMainBinding.inflate(getLayoutInflater());
		View view = mBinding.getRoot();
		setContentView(view);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
			this, R.array.operations_array, android.R.layout.simple_spinner_item
		);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBinding.operationSpinner.setAdapter(spinnerAdapter);

		mBinding.sendButton.setOnClickListener(sendRequestButton);
	}

	private View.OnClickListener sendRequestButton = view -> {
		mBinding.sendButton.setEnabled(false);
		String host = mBinding.hostEditText.getText().toString();
		String port = mBinding.portEditText.getText().toString();
		String firstNumber = mBinding.firstNumberEditText.getText().toString();
		String secondNumber = mBinding.secondNumberEditText.getText().toString();
		String operation = mBinding.operationSpinner.getSelectedItem().toString();
		new GrpcTask(this).execute(host, port, firstNumber, secondNumber, operation);
	};

	private class GrpcTask extends AsyncTask<String, Void, String> {

		private final WeakReference<MainActivity> mActivityReference;

		private ManagedChannel mChannel;

		private GrpcTask(MainActivity activity) {
			mActivityReference = new WeakReference<>(activity);
		}

		@Override
		protected String doInBackground(String... params) {
			String host = params[0];
			int port = TextUtils.isEmpty(params[1]) ? 0 : Integer.parseInt(params[1]);
			int firstNumber = TextUtils.isEmpty(params[2]) ? 0 : Integer.parseInt(params[2]);
			int secondNumber = TextUtils.isEmpty(params[3])? 0 :Integer.parseInt(params[3]);
			String operation = params[4];

			try {
				mChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
				ExampleGrpc.ExampleBlockingStub stub = ExampleGrpc.newBlockingStub(mChannel);
				ExampleRequest request = ExampleRequest.newBuilder()
					.setFirstNumber(firstNumber)
					.setSecondNumber(secondNumber)
					.build();
				ExampleReply reply = null;
				switch (operation) {
					case "Add":
						reply = stub.doAddition(request);
						break;
					case "Subtract":
						reply = stub.doSubtraction(request);
						break;
					case "Multiply":
						reply = stub.doMultiplication(request);
						break;
					case "Divide":
						reply = stub.doDivision(request);
						break;
				}
				return reply.getResult();
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace();
				pw.flush();
				return String.valueOf(sw);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			MainActivity activity = mActivityReference.get();
			if (activity == null) {
				return;
			}
			activity.mBinding.grpcResponseText.setText(result);
			activity.mBinding.sendButton.setEnabled(true);
		}
	}
}