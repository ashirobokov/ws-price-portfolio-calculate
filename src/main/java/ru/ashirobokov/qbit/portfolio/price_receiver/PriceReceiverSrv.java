package ru.ashirobokov.qbit.portfolio.price_receiver;

import io.advantageous.qbit.client.Client;
import io.advantageous.qbit.client.ClientBuilder;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceReceiverSrv {
    public final static Logger LOG = LoggerFactory.getLogger(PriceReceiverSrv.class);
    private final static PriceReceiverSrv _INSTANSE = new PriceReceiverSrv();
    /**
     * Client service proxy to the PriceReceiverImpl
     */
    private final static String priceRcvAddress = "priceRcvSrv";
    private ServiceEndpointServer priceRcvSrvEndpointServer;
    private PriceReceiverImpl priceReceiver;
    /**
     * QBit WebSocket Client
     */
    private Client webSocketClient;
    private PriceReceiver proxy;

    private PriceReceiverSrv() {
        LOG.info("PriceReceiverSrv...starting...");
        try {

            /* Create the EndpointServerBuilder. */
            final EndpointServerBuilder endpointServerBuilder = new EndpointServerBuilder();

            /* Create the service server. */
            priceRcvSrvEndpointServer = endpointServerBuilder
                    .setHost("127.0.0.1")
                    .setPort(8080)
                    .setHealthService(null)
                    .setEnableHealthEndpoint(false)
                    .build();

            /* Create a PriceReceiverImpl */
            priceReceiver = new PriceReceiverImpl();
            // Add the priceReceiver to the serviceBundle.
            priceRcvSrvEndpointServer.serviceBundle()
                    .addServiceObject(priceRcvAddress, priceReceiver)
                    .startServiceBundle();

            //Create a LOCAL proxy to communicate with the service actor.
//            proxy = priceRcvSrvEndpointServer.serviceBundle().createLocalProxy(PriceReceiver.class, priceRcvAddress);

            //Create a handler to filter and process received prices
            PriceHandler priceHandler = new PriceHandler(priceReceiver);
            priceHandler.processing();

            /* Start the service endpoint server and wait until it starts. */
            priceRcvSrvEndpointServer.startServerAndWait();

            /* Create the WebSocket Client. */
            final ClientBuilder clientBuilder = ClientBuilder.clientBuilder();

            /** Build the webSocketClient. */
            webSocketClient = clientBuilder
                    .setHost("127.0.0.1")
                    .setPort(8080)
                    .setAutoFlush(true)
                    .setFlushInterval(1)
                    .setProtocolBatchSize(100)
                    .build();

            /* Create a REMOTE proxy proxy to communicate with the service actor. */
            proxy = webSocketClient.createProxy(PriceReceiver.class, priceRcvAddress);
            /* Start the remote proxy. */
            webSocketClient.start();

        } catch (Exception e) {
            LOG.error("PriceReceiverSrv...start error");
        }
        LOG.info("/PriceReceiverSrv...started OK");
    }

    public static PriceReceiverSrv getInstanse() {
        return _INSTANSE;
    }

    public PriceReceiver getProxy() {
        return proxy;
    }
}
