# Android gRPC Example

This project is based on [grpc-java](https://github.com/grpc/grpc-java) with some modifications regarding to the defined RPC services.

## Prerequisites

- JDK version 8 or higher
- Android SDK, API level 24 or higher

## Running the Project

1. Compile the server:

    Unix-based:

    ```shell
    ./Server/gradlew installDist
    ```

    Windows:

    ```cmd
    cd Server
    gradlew installDist
    ```

2. Run the server:

    Unix-based:

    ```shell
    ./Server/build/install/gRPC-server/bin/example-server
    ```

    Windows:

    ```cmd
    cd Server/build/install/gRPC-server/bin/
    example-server
    ```

    If started successfully, the server will print

    ```shell
    INFO: Server started, listening on 50051
    ```

    Take note that `50051` is the server port.

3. Build and run the `Android-Client` project through an Android Virtual Device.

4. In the app, fill `10.0.2.2` and `50051` to Host and Port fields respectively.

5. Fill the required fields and hit `Send gRPC Request` button to see the results on the layout.