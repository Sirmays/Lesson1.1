package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {

    public static void main(String[] args) throws IOException {
        new Server().start();

    }

    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress("localhost", 9000));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started");

        while (true) {
            selector.select();
            System.out.println("New selector event:");
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    System.out.println("New selector acceptable event:");
                    register(selector, serverSocket);
                }

                if (selectionKey.isReadable()) {
                    System.out.println("New selector readable event:");
                    readMessage(selectionKey);
                }
                iterator.remove();
            }
        }
    }

    public void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("New client is connected");
    }

    public void readMessage(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(50);
        client.read(byteBuffer);
        byteBuffer.flip();
        byte[] temp_message = new byte[byteBuffer.limit()];
        byteBuffer.get(temp_message);
        String message = (new String(temp_message));
        System.out.println("Message received: " + message);
        client.write(ByteBuffer.wrap(message.getBytes()));

    }
}
