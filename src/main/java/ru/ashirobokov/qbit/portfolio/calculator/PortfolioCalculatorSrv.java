package ru.ashirobokov.qbit.portfolio.calculator;

import io.advantageous.qbit.client.Client;
import io.advantageous.qbit.client.ClientBuilder;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortfolioCalculatorSrv {
    public final static Logger LOG = LoggerFactory.getLogger(PortfolioCalculatorSrv.class);
    private final static PortfolioCalculatorSrv _INSTANSE = new PortfolioCalculatorSrv();
    /**
     * Client service proxy to the portfolioCalculatorImpl
     */
    private final static String portfolioCalcAddress = "portfolioCalcSrv";
    private ServiceEndpointServer portfolioCalcSrvEndpointServer;
    private PortfolioCalculatorImpl portfolioCalculator;
    /**
     * QBit WebSocket Client
     */
    private Client webSocketClient;
    private PortfolioCalculator proxy;

    private PortfolioCalculatorSrv() {
        LOG.info("PortfolioCalculatorSrv...starting...");
        try {

            /* Create the EndpointServerBuilder. */
            final EndpointServerBuilder endpointServerBuilder = new EndpointServerBuilder();

            /* Create the service server. */
            portfolioCalcSrvEndpointServer = endpointServerBuilder
                    .setHost("127.0.0.1")
                    .setPort(8085)
                    .setHealthService(null)
                    .setEnableHealthEndpoint(false)
                    .build();

            /* Create a PortfolioCalculatorImpl */
            portfolioCalculator = new PortfolioCalculatorImpl();
            // Add the portfolioCalculator to the serviceBundle.
            portfolioCalcSrvEndpointServer.serviceBundle()
                    .addServiceObject(portfolioCalcAddress, portfolioCalculator)
                    .startServiceBundle();

            //Create a proxy proxy to communicate with the service actor.
            proxy = portfolioCalcSrvEndpointServer.serviceBundle().createLocalProxy(PortfolioCalculator.class, portfolioCalcAddress);

            CalculatorHandler calculatorHandler = new CalculatorHandler(portfolioCalculator);
            calculatorHandler.init();
            calculatorHandler.calculating();

            /* Start the service endpoint server and wait until it starts. */
            portfolioCalcSrvEndpointServer.startServerAndWait();

            /* Create the WebSocket Client. */
            final ClientBuilder clientBuilder = ClientBuilder.clientBuilder();

            /** Build the webSocketClient. */
            webSocketClient = clientBuilder
                    .setHost("127.0.0.1")
                    .setPort(8085)
                    .setAutoFlush(true)
                    .setFlushInterval(1)
                    .setProtocolBatchSize(100)
                    .build();

            /* Create a REMOTE proxy proxy to communicate with the service actor. */
            proxy = webSocketClient.createProxy(PortfolioCalculator.class, portfolioCalcAddress);
            /* Start the remote proxy. */
            webSocketClient.start();


        } catch (Exception e) {
            LOG.error("PortfolioCalculatorSrv...start error");
        }
        LOG.info("/PortfolioCalculatorSrv...started OK");

    }

    public static PortfolioCalculatorSrv getInstanse() {
        return _INSTANSE;
    }

    public PortfolioCalculator getProxy() {
        return proxy;
    }
}
