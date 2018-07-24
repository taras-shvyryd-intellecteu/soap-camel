package my.demo.route;

import model.Multiply;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.camel.dataformat.soap.SoapJaxbDataFormat;

/**
 * Created on 19.07.18 by taras
 */
public class SoapClientRoute extends RouteBuilder {

    @Override
    public void configure() {
        CamelContext camelContext = getContext();

        CxfEndpoint cxfEndpoint = new CxfEndpoint();
        cxfEndpoint.setAddress("http://www.dneonline.com/calculator.asmx");
        cxfEndpoint.setCamelContext(camelContext);
        cxfEndpoint.setDataFormat(DataFormat.RAW);

        try {
            cxfEndpoint.setServiceClass("model.Calculator");
            camelContext.addEndpoint("calculator", cxfEndpoint);
        } catch (Exception e) { e.printStackTrace(); }

        Multiply mul = new Multiply();
        mul.setIntA(5);
        mul.setIntB(18);
        SoapJaxbDataFormat soapDF = new SoapJaxbDataFormat("model");

        from("timer:foo?period=60000&repeatCount=1")
                .process(exchange -> exchange.getIn().setBody(mul))
                .marshal(soapDF)
                .log("${body}")
                .setHeader("Content-Type", constant("text/xml"))
                .to("calculator")
                .unmarshal(soapDF)
                .log("Result: ${body.getMultiplyResult()}");
    }
}
