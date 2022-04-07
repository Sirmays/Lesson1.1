package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        try {
            new Client().start();
        } finally {
            THREAD_POOL.shutdown();
        }
    }

    public void start() {
        THREAD_POOL.execute(() -> {
            System.out.println("New client started on thread " + Thread.currentThread().getName());
            try {
                SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost", 9000));
                while (true) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(50);
                    Scanner scanner = new Scanner(System.in);
                    String message = scanner.nextLine();
                    channel.write(ByteBuffer.wrap(message.getBytes()));

                    channel.read(byteBuffer);
                    byteBuffer.flip();
                    byte[] temp_message = new byte[byteBuffer.limit()];
                    byteBuffer.get(temp_message);
                    String echo_message = (new String(temp_message));
                    System.out.println("Echo: " + echo_message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
