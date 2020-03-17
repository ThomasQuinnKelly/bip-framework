
package gov.va.bip.framework.client.ws.remote.test.mocks;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

public class Adapter1
    extends XmlAdapter<String, Date>
{


    public Date unmarshal(String value) {
        return (gov.va.bip.framework.transfer.jaxb.adapters.DateAdapter.parseDateTime(value));
    }

    public String marshal(Date value) {
        return (gov.va.bip.framework.transfer.jaxb.adapters.DateAdapter.printDateTime(value));
    }

}
