package io.grpc.examples;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExampleServer {

	private static final Logger LOG = Logger.getLogger(ExampleServer.class.getName());

	private final int PORT = 50051;

	private Server server;

	private void start() throws IOException {
		this.server = ServerBuilder.forPort(this.PORT)
			.addService(new ExampleServiceImpl())
			.build()
			.start();
		LOG.info("Server started, listening on " + this.PORT);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				try {
					ExampleServer.this.stop();
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
				System.err.println("*** server shut down");
			}
		});
	}

	private void stop() throws InterruptedException {
		if (this.server != null) {
			this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
		}
	}

	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	private static class ExampleServiceImpl extends ExampleGrpc.ExampleImplBase {

		@Override
		public void doAddition(ExampleRequest request, StreamObserver<ExampleReply> responseObserver) {
			int firstNumber = request.getFirstNumber();
			int secondNumber = request.getSecondNumber();
			String result = String.valueOf(firstNumber + secondNumber);
			ExampleReply reply = ExampleReply.newBuilder().setResult(result).build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}

		@Override
		public void doSubtraction(ExampleRequest request, StreamObserver<ExampleReply> responseObserver) {
			int firstNumber = request.getFirstNumber();
			int secondNumber = request.getSecondNumber();
			String result = String.valueOf(firstNumber - secondNumber);
			ExampleReply reply = ExampleReply.newBuilder().setResult(result).build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}

		@Override
		public void doMultiplication(ExampleRequest request, StreamObserver<ExampleReply> responseObserver) {
			int firstNumber = request.getFirstNumber();
			int secondNumber = request.getSecondNumber();
			String result = String.valueOf(firstNumber * secondNumber);
			ExampleReply reply = ExampleReply.newBuilder().setResult(result).build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}

		@Override
		public void doDivision(ExampleRequest request, StreamObserver<ExampleReply> responseObserver) {
			int firstNumber = request.getFirstNumber();
			int secondNumber = request.getSecondNumber();
			String result;
			try {
				result = String.valueOf(firstNumber / secondNumber);
			} catch (ArithmeticException e) {
				result = "Division by Zero Error";
			}
			ExampleReply reply = ExampleReply.newBuilder().setResult(result).build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		final ExampleServer server = new ExampleServer();
		server.start();
		server.blockUntilShutdown();
	}
}
