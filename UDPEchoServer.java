import java.io.*;
import java.net.*;
import java.util.logging.*;


abstract class UDPServer implements Runnable {
  private final int bufferSize;
  private final int port;
  private final Logger logger = Logger.getLogger(UDPServer.class.getCanonicalName());
  private volatile boolean isShutDown = false;

  public UDPServer(int port, int bufferSize) {
    this.bufferSize = bufferSize;
    this.port = port;
  }

  public UDPServer(int port) {
    this(port, 8192);
  }

  @Override
  public void run() {
    byte[] buffer = new byte[bufferSize];
    try (DatagramSocket socket = new DatagramSocket(port)) {
      socket.setSoTimeout(10000); // Chcek every 10 seconds for shutdown

      while(true) {
        if (isShutDown) return;
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

        try {
          socket.receive(incoming);
          this.respond(socket, incoming);

        } catch (SocketTimeoutException e) {
          if (isShutDown) return;
        } catch (IOException e) {
          logger.log(Level.WARNING, e.getMessage(), e);
        }
      } // end while
    } catch (SocketException e) {
      logger.log(Level.SEVERE, "Could not bind to port: " + port, e);
    }
  }

  public abstract void respond(DatagramSocket socket, DatagramPacket request)
  throws IOException;

  public void shutDown() {
    this.isShutDown = true;
  }

}

public class UDPEchoServer extends UDPServer {

  public final static int DEFAULT_PORT = 7;

  public UDPEchoServer() {
    super(DEFAULT_PORT);
  }

  @Override
  public void respond(DatagramSocket socket, DatagramPacket packet) throws IOException {
    DatagramPacket outgoing = new DatagramPacket(packet.getData(),
      packet.getLength(), packet.getAddress(), packet.getPort());
    socket.send(outgoing);
  }

  public static void main(String[] args) {
    UDPServer server = new UDPEchoServer();
    Thread t = new Thread(server);
    t.start();
  }
}
